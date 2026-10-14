import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * MacroCalculator —— 简易宏量营养素计算器（Java 版）
 *
 * 读取 nutrition-data.csv（每 100g 营养数据），按编号选择食材并输入重量(g)，
 * 计算一顿饭的总热量与三大营养素（蛋白质 / 脂肪 / 碳水）。
 *
 * 用法：
 *     javac MacroCalculator.java
 *     java  MacroCalculator
 *
 * 说明：用「编号」选择食材，避免中文在控制台输入时的编码问题。
 * 数据来源：同目录 nutrition-data.csv（从个人资料库营养表导出）
 * 作者：chenjunwei-Klein
 */
public class MacroCalculator {

    /** 一种食材：每 100g 的营养数据 */
    static class Food {
        String name;
        double kcal, protein, fat, carb;

        Food(String name, double kcal, double protein, double fat, double carb) {
            this.name = name;
            this.kcal = kcal;
            this.protein = protein;
            this.fat = fat;
            this.carb = carb;
        }
    }

    public static void main(String[] args) throws IOException {
        List<Food> foods = loadFoods("nutrition-data.csv");
        System.out.println("已加载食材: " + foods.size() + " 种\n");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));

        // 打印编号列表，用户输入编号即可
        for (int i = 0; i < foods.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, foods.get(i).name);
        }
        System.out.println("\n输入格式：编号 重量(g)，例如「1 180」；输入空行结束。\n");

        double tKcal = 0, tProtein = 0, tFat = 0, tCarb = 0;

        while (true) {
            System.out.print("> ");
            String line = in.readLine();
            if (line == null || line.trim().isEmpty()) {
                break;
            }
            String[] parts = line.trim().split("\\s+");
            if (parts.length != 2) {
                System.out.println("格式不对，请按「编号 重量(g)」输入。");
                continue;
            }

            int idx;
            double grams;
            try {
                idx = Integer.parseInt(parts[0]);
                grams = Double.parseDouble(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("请输入数字。");
                continue;
            }
            if (idx < 1 || idx > foods.size()) {
                System.out.println("编号超出范围。");
                continue;
            }

            Food f = foods.get(idx - 1);
            double factor = grams / 100.0;
            double kcal = f.kcal * factor;
            double protein = f.protein * factor;
            double fat = f.fat * factor;
            double carb = f.carb * factor;

            tKcal += kcal;
            tProtein += protein;
            tFat += fat;
            tCarb += carb;

            System.out.printf("  + %s %.0fg -> %.0f kcal / 蛋白 %.1fg / 脂肪 %.1fg / 碳水 %.1fg%n",
                    f.name, grams, kcal, protein, fat, carb);
        }

        System.out.println("\n===== 合计 =====");
        System.out.printf("热量 : %.0f kcal%n", tKcal);
        System.out.printf("蛋白质: %.1f g%n", tProtein);
        System.out.printf("脂肪 : %.1f g%n", tFat);
        System.out.printf("碳水 : %.1f g%n", tCarb);
    }

    /** 读取 CSV，返回食材列表（跳过表头，按逗号简单分割） */
    private static List<Food> loadFoods(String file) throws IOException {
        List<Food> list = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {
            r.readLine(); // 跳过表头
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] c = line.split(",");
                if (c.length < 5) {
                    continue;
                }
                list.add(new Food(
                        c[0].trim(),
                        Double.parseDouble(c[1]),
                        Double.parseDouble(c[2]),
                        Double.parseDouble(c[3]),
                        Double.parseDouble(c[4])));
            }
        }
        return list;
    }
}
