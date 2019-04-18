import java.util.Random;
import java.util.Scanner;

public class MineCleaning {

    public static void main(String[] args) {
        MapManager mm = new MapManager();
        mm.StartMenu();
    }

}

class MapManager {
    public void StartMenu() {
        DrawStartMenu();
        AnyInputGoToMainMenu();
    }

    public void AnyInputGoToMainMenu() {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()) MainMenu();
    }

    public void MainMenu() {
        DrawMainMenu();
        MainMenuChoice();
    }

    private void DrawStartMenu() {
        System.out.println("【扫雷游戏  v0.1】");
        System.out.println(" 开发者：朱肖訸  ");
        System.out.println("[任意输入开始游戏]");
    }

    private void DrawMainMenu() {
        System.out.println("===============================================");
        System.out.println("【主菜单】");
        System.out.println("1、开始游戏  2、游戏设置  3、版本介绍  4、退出游戏");
    }

    private void MainMenuChoice() {
        System.out.println("请输入你的选择:");
        Scanner scanner = new Scanner(System.in);
        String select = scanner.nextLine();
        switch (select) {
            case "1":
                GameInit();
                break;
            case "2":
                OptionMenu();
                break;
            case "3":
                VersionMenu();
                break;
            case "4":
                System.exit(0);
                break;
            default:
                MainMenuChoice();
        }
    }

    private void GameInit() {
        System.out.println("【开始游戏】");
        System.out.println("本游戏版本目前仅支持9*9初级难度");
        Gaming game = new Gaming();
        ChooseDig(game);
    }

    private void ChooseDig(Gaming aGame) {
        System.out.println("请选择要挖开的区块（书写格式，如：“1,2”，1代表第一行，2代表第二列）：");
        System.out.println("（输入'0'就退出游戏）");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        if (input.equals("0")) System.exit(0);
        else {
            if (CorrectInput(input)) {
                aGame.DrawUserMineMapMask(input);
                ChooseDig(aGame);
            } else {
                System.out.println("看清楚输入格式好吗？输入1~9的数字+英文半角逗号+1~9的数字，兄弟！");
                ChooseDig(aGame);
            }
        }
    }

    private void OptionMenu() {
        System.out.println("【游戏设置】");
        System.out.println("暂不支持本功能");
        System.out.println("任意输入返回上一级菜单~");
        AnyInputGoToMainMenu();

    }

    private void VersionMenu() {
        System.out.println("版本介绍：");
        System.out.println("v0.1:制作了基本的界面——开始菜单、主菜单等，实现了雷图的生成和用户视野掩码的制作");
        System.out.println("任意输入返回上一级菜单~");
        AnyInputGoToMainMenu();
    }

    private boolean CorrectInput(String aInput) {
        if (aInput.length() == 3 && aInput.charAt(1) == ','
                && Character.isDigit(aInput.charAt(0)) && Character.isDigit(aInput.charAt(2))
                && aInput.charAt(0) != '0' && aInput.charAt(2) != 0) {
            return true;
        } else {
            return false;
        }
    }


}

class Gaming {
    int[][] MineMap;
    boolean[][] MineMapMask;

    Gaming() {
        InitMineMap();
        MineMapMask = new boolean[11][11];
    }

    private void InitMineMap() {
        System.out.println("正在生成雷图……");
        MineGenerator();
        NumGenerator();
        System.out.println("已生成雷图");
    }

    void DrawUserMineMapMask(String aInput) {
        int col = Integer.parseInt(aInput.substring(2, 3));//列
        int row = Integer.parseInt(aInput.substring(0, 1));//行
        UserMineMapMaskAfterDig(row, col);

    }

    private void UserMineMapMaskAfterDig(int aRow, int aCol) {
        if (MineMapMask[aRow][aCol] == true) {
            System.out.println("你特么已经挖过[" + aRow + "，" + aCol + "]了！！！快重新选一个！");
        } else {
            MineMapMask[aRow][aCol] = true;
            for (int i = 1; i <= 9; i++) {
                for (int j = 1; j <= 9; j++) {
                    if (MineMapMask[i][j] == false) {
                        System.out.print("[  ]");
                    } else {
                        System.out.printf("[% 2d]", MineMap[i][j]);
                    }
                }
                System.out.println();
            }
        }
    }

    private void MineGenerator() {
        MineMap = new int[11][11];
        boolean flag = true;
        Random rand = new Random();
        int count = 0;
        while (flag) {
            int row = rand.nextInt(9) + 1;
            int col = rand.nextInt(9) + 1;
            if (MineMap[row][col] != 9) {
                MineMap[row][col] = 9;
                System.out.println("哈哈，埋个地雷在[" + row + "," + col + "]！");//TODO:测试完了记得删掉
                count++;
            }
            if (count == 10) {
                flag = false;
            }
        }
    }

    private void NumGenerator() {
        int[][] offset = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                if (MineMap[i][j] != 9) {
                    int countBomb = 0;
                    for (int k = 0; k < 8; k++) {
                        if (MineMap[i + offset[k][0]][j + offset[k][1]] == 9) {
                            countBomb++;
                        }
                    }
                    MineMap[i][j] = countBomb;
                }
            }
        }
    }
}