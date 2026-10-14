"""
macro_calculator.py — 简易宏量营养素计算器

功能：
    根据食材的「每 100g 营养数据」和「实际吃了多少克」，
    计算一顿饭 / 一天的总热量和蛋白/脂肪/碳水摄入。

用法：
    python macro_calculator.py
    按提示输入「食物名 重量(g)」，输入空行结束，即可看到合计。

数据来源：同目录下的 nutrition-data.csv（从个人资料库营养表导出）
作者：chenjunwei-Klein
"""

import csv
from pathlib import Path

DATA_FILE = Path(__file__).parent / "nutrition-data.csv"


def load_foods(path: Path) -> dict:
    """读取 CSV，返回 {食物名: {热量, 蛋白质, 脂肪, 碳水}}（均为每 100g 的值）"""
    foods = {}
    with path.open(encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            name = row["食物"].strip()
            foods[name] = {
                "kcal": float(row["热量_kcal"]),
                "protein": float(row["蛋白质_g"]),
                "fat": float(row["脂肪_g"]),
                "carb": float(row["碳水_g"]),
            }
    return foods


def main() -> None:
    foods = load_foods(DATA_FILE)
    print("已加载食材:", len(foods), "种")
    print("输入格式：食物名 重量(g)，例如「鸡胸肉（生，去皮） 180」")
    print("输入空行结束计算。\n")

    total = {"kcal": 0.0, "protein": 0.0, "fat": 0.0, "carb": 0.0}

    while True:
        line = input("> ").strip()
        if not line:
            break
        try:
            name, grams = line.rsplit(maxsplit=1)
            grams = float(grams)
        except ValueError:
            print("格式不对，请按「食物名 重量(g)」输入。")
            continue

        if name not in foods:
            print(f"没找到「{name}」，可用食材见 nutrition-data.csv。")
            continue

        f = foods[name]
        factor = grams / 100.0
        kcal = f["kcal"] * factor
        protein = f["protein"] * factor
        fat = f["fat"] * factor
        carb = f["carb"] * factor

        total["kcal"] += kcal
        total["protein"] += protein
        total["fat"] += fat
        total["carb"] += carb

        print(f"  + {name} {grams:g}g → "
              f"{kcal:.0f} kcal / 蛋白 {protein:.1f}g / 脂肪 {fat:.1f}g / 碳水 {carb:.1f}g")

    print("\n===== 合计 =====")
    print(f"热量 : {total['kcal']:.0f} kcal")
    print(f"蛋白质: {total['protein']:.1f} g")
    print(f"脂肪 : {total['fat']:.1f} g")
    print(f"碳水 : {total['carb']:.1f} g")


if __name__ == "__main__":
    main()
