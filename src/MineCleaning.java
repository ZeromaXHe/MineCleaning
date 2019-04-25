import java.util.Random;
import java.util.Scanner;

public class MineCleaning {

    public static void main(String[] args) {
        MapManager mm = new MapManager();
        mm.startMenu();
    }

}

class MapManager {
    public void startMenu() {
        drawStartMenu();
        anyInputGoToMainMenu();
    }

    public void anyInputGoToMainMenu() {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()) mainMenu();
    }

    public void mainMenu() {
        drawMainMenu();
        mainMenuChoice();
    }

    private void drawStartMenu() {
        System.out.println("【扫雷游戏  v0.2】");
        System.out.println(" 开发者：朱肖訸  ");
        System.out.println("[任意输入开始游戏]");
    }

    private void drawMainMenu() {
        System.out.println("===============================================");
        System.out.println("【主菜单】");
        System.out.println("1、开始游戏  2、游戏设置  3、版本介绍  4、退出游戏");
    }

    private void mainMenuChoice() {
        System.out.println("请输入你的选择:");
        Scanner scanner = new Scanner(System.in);
        String select = scanner.nextLine();
        switch (select) {
            case "1":
                gameInit();
                break;
            case "2":
                optionMenu();
                break;
            case "3":
                versionMenu();
                break;
            case "4":
                System.exit(0);
                break;
            default:
                mainMenuChoice();
        }
    }

    private void gameInit() {
        System.out.println("【开始游戏】");
        System.out.println("本游戏版本目前仅支持9*9初级难度");
        Gaming game = new Gaming();
        chooseDig(game);
    }

    private void chooseDig(Gaming aGame) {
        System.out.println("请选择要挖开的区块（书写格式，如：“1,2”，1代表第一行，2代表第二列）：");
        System.out.println("（输入'0'就退出游戏）");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        if (input.equals("0")) System.exit(0);
        else {
            if (correctInput(input)) {

                if(aGame.drawUserMineMapMask(input)!=true && aGame.isLoseFlag()!=true) chooseDig(aGame);
                else {
                    if(aGame.isLoseFlag()==true) {
                        System.out.println("对不起，欢迎再来挑战！祝你下次成功！");
                    }
                    else {
                        System.out.println("你赢了，牛逼啊~");
                    }
                    System.out.println("[任意输入返回主菜单]");
                    anyInputGoToMainMenu();
                }
            } else {
                System.out.println("看清楚输入格式好吗？输入1~9的数字+英文半角逗号+1~9的数字，兄弟！");
                chooseDig(aGame);
            }
        }
    }

    private void optionMenu() {
        System.out.println("【游戏设置】");
        System.out.println("暂不支持本功能");
        System.out.println("任意输入返回上一级菜单~");
        anyInputGoToMainMenu();

    }

    private void versionMenu() {
        System.out.println("版本介绍：");
        System.out.println("v0.1:制作了基本的界面——开始菜单、主菜单等，实现了雷图的生成和用户视野掩码的制作");
        System.out.println("v0.2:制作了点中周围无地雷的地块时自动遍历周边地区的功能，实现了游戏失败、成功的判定，基本可以进行真正的游戏");
        System.out.println("任意输入返回上一级菜单~");
        anyInputGoToMainMenu();
    }

    /**
     * 检查输入的合法性
     *
     * @param aInput
     * @return true，当输入符合格式要求；false，当输入不符合要求
     */
    private boolean correctInput(String aInput) {
        if (aInput.length() == 3 && aInput.charAt(1) == ','
                && Character.isDigit(aInput.charAt(0)) && Character.isDigit(aInput.charAt(2))
                && aInput.charAt(0) != '0' && aInput.charAt(2) != '0') {
            return true;
        } else {
            return false;
        }
    }


}

class Gaming {
    private int[][] mineMap;
    private boolean[][] mineMapMask;
    private int[][] offset = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    private boolean loseFlag;
    private int remainingMine;
    private int remainingUndig;

    Gaming() {
        loseFlag=false;
        initMineMap();
        mineMapMask = new boolean[11][11];
        setBoundary();
        remainingMine = 10;
        remainingUndig = 81;
    }

    private void initMineMap() {
        System.out.println("正在生成雷图……");
        mineGenerator();
        numGenerator();
        System.out.println("已生成雷图");
    }

    /**
     * 负责生成雷图
     */
    private void mineGenerator() {
        mineMap = new int[11][11];
        boolean flag = true;
        Random rand = new Random();
        int count = 0;
        while (flag) {
            int row = rand.nextInt(9) + 1;
            int col = rand.nextInt(9) + 1;
            if (mineMap[row][col] != 9) {
                mineMap[row][col] = 9;
                //System.out.println("哈哈，埋个地雷在[" + row + "," + col + "]！");//测试用语句
                count++;
            }
            if (count == 10) {
                flag = false;
            }
        }
    }

    /**
     * 负责生成显示周边地雷的数字
     */
    private void numGenerator() {
        int[][] offset = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                if (mineMap[i][j] != 9) {
                    int countBomb = 0;
                    for (int k = 0; k < 8; k++) {
                        if (mineMap[i + offset[k][0]][j + offset[k][1]] == 9) {
                            countBomb++;
                        }
                    }
                    mineMap[i][j] = countBomb;
                }
            }
        }
    }

    private void setBoundary() {
        for (int i = 0; i < 11; i++) {
            mineMapMask[i][0] = mineMapMask[0][i] = mineMapMask[i][10] = mineMapMask[10][i] = true;
        }
    }

    public boolean isLoseFlag() {
        return loseFlag;
    }

    boolean drawUserMineMapMask(String aInput) {
        int col = Integer.parseInt(aInput.substring(2, 3));//列
        int row = Integer.parseInt(aInput.substring(0, 1));//行
        if(userMineMapMaskAfterDig(row, col)==false) {
            displayMineMap();
            return false;
        }
        else return true;
    }

    /**
     * 负责在每一次挖掘后生成用户视野掩码
     *
     * @param aRow
     * @param aCol
     * @return true，如果赢了；false，还没赢
     */
    private boolean userMineMapMaskAfterDig(int aRow, int aCol) {
        if (mineMapMask[aRow][aCol] == true) {
            System.out.println("你特么已经挖过[" + aRow + "，" + aCol + "]了！！！快重新选一个！");
        } else {
            if(mineMap[aRow][aCol]==9) {
                loseFlag=true;
                dig(aRow,aCol);
                System.out.println("你输了，[" + aRow + "，" + aCol + "]是地雷");
            }
            else {
                dig(aRow, aCol);
            }
        }

        return checkWinOrNot();
    }

    private boolean checkWinOrNot(){
        if(remainingUndig==remainingMine) return true;
        else return false;
    }

    private void dig(int aRow, int aCol) {
        mineMapMask[aRow][aCol] = true;
        if(mineMap[aRow][aCol]!=9)remainingUndig--;
        if (mineMap[aRow][aCol] == 0) {
            for (int i = 0; i < 8; i++) {
                //System.out.println("正在Dig[" + (aRow + offset[i][0]) + "][" + (aCol + offset[i][1]) + "]");//测试用语句
                if (mineMapMask[aRow + offset[i][0]][aCol + offset[i][1]] == false)
                    dig(aRow + offset[i][0], aCol + offset[i][1]);
            }
        }

    }

    private void displayMineMap() {
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 9; j++) {
                if (mineMapMask[i][j] == false) {
                    System.out.print("[  ]");
                } else {
                    System.out.printf("[% 2d]", mineMap[i][j]);
                }
            }
            System.out.println();
        }
    }


}