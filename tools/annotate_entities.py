#!/usr/bin/env python3
"""
Recorre src/main/java buscando paquetes 'entities' y añade:
 - import jakarta.persistence.Column;  (si falta)
 - @Column(name = "campo", nullable = false) antes de campos que no tengan @Column/@JoinColumn/@Id/@Transient/@EmbeddedId
Heurística nullable=false:
 - tipos primitivos (int, long, boolean, double, float, short, byte, char)
 - campos anotados con @NotNull o @NonNull

Revisa siempre los cambios antes de commit.
"""
import os
import re
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parent.parent  # ../..
SRC_ROOT = PROJECT_ROOT / "src" / "main" / "java"

PRIMITIVES = {"int", "long", "short", "byte", "float", "double", "boolean", "char"}

ANNOTATION_SKIP = {"@Column", "@JoinColumn", "@Id", "@Transient", "@EmbeddedId", "@Embedded", "@MappedSuperclass"}

RELATION_ANN = {"@OneToOne", "@OneToMany", "@ManyToOne", "@ManyToMany", "@ElementCollection"}


field_re = re.compile(r'^\s*(private|protected|public)\s+([^\s;]+(?:\s*<[^>]+>)?)\s+([A-Za-z_][A-Za-z0-9_]*)\s*;')

def process_file(path: Path):
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines()
    modified = False

    # find imports
    has_column_import = any(re.match(r'\s*import\s+jakarta\.persistence\.Column\s*;', l) for l in lines)
    # If file uses jakarta.persistence.* already, still add explicit Column import if missing
    has_any_jakarta = any('import jakarta.persistence' in l for l in lines)

    # find package and last import index to insert import if needed
    last_import_idx = None
    for i, l in enumerate(lines):
        if l.strip().startswith('import '):
            last_import_idx = i

    if not has_column_import and has_any_jakarta:
        insert_at = last_import_idx + 1 if last_import_idx is not None else 1
        lines.insert(insert_at, 'import jakarta.persistence.Column;')
        modified = True

    i = 0
    while i < len(lines):
        m = field_re.match(lines[i])
        if m:
            field_type = m.group(2).strip()
            field_name = m.group(3).strip()
            # collect previous contiguous annotation lines (immediately above)
            j = i - 1
            ann_block = []
            while j >= 0 and lines[j].strip().startswith("@"):
                ann_block.insert(0, lines[j].strip())
                j -= 1
            ann_set = set(ann_block)
            # if any skip annotations present -> do not add @Column
            if any(a.startswith(tuple(ANNOTATION_SKIP)) for a in ann_block) or any(a.startswith(tuple(RELATION_ANN)) for a in ann_block):
                i += 1
                continue
            # Also skip if @Column already present in the file nearby (safety)
            if any('@Column' in a for a in ann_block):
                i += 1
                continue
            # decide nullable
            # remove generics and array markers for type base
            base_type = re.sub(r'<.*?>', '', field_type).strip()
            base_type = base_type.replace("[]", "").strip()
            nullable_false = False
            if base_type in PRIMITIVES:
                nullable_false = True
            if any(a.lower().startswith(('@notnull', '@nonnull')) for a in ann_block):
                nullable_false = True
            # build annotation
            col_parts = [f'name = "{field_name}"']
            if nullable_false:
                col_parts.append("nullable = false")
            col_ann = f"@Column({', '.join(col_parts)})"
            # insert annotation above the field (after any j+1)
            insert_pos = i
            # if there are annotations above, place after them; else at position i
            if j < i - 1:
                insert_pos = j + 1 + len(ann_block)
            lines.insert(insert_pos, "    " + col_ann if lines[i].startswith("    ") else col_ann)
            i += 1  # skip the inserted line
            modified = True
        i += 1

    if modified:
        path.write_text("\n".join(lines) + "\n", encoding="utf-8")
        print(f"Modified: {path}")
    else:
        print(f"Unchanged: {path}")

def find_entity_dirs(root: Path):
    for dirpath, dirnames, filenames in os.walk(root):
        if Path(dirpath).name == "entities":
            yield Path(dirpath)

def main():
    if not SRC_ROOT.exists():
        print(f"source root not found: {SRC_ROOT}")
        return
    for ent_dir in find_entity_dirs(SRC_ROOT):
        for java_file in ent_dir.glob("**/*.java"):
            process_file(java_file)

if __name__ == "__main__":
    main()
