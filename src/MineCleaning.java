import java.util.Random;
import java.util.Scanner;

public class MineCleaning {

    public static void main(String[] args) {
        MapManager mm = new MapManager();
        mm.startMenu();
    }

}

class MapManager {
    void startMenu() {
        drawStartMenu();
        anyInputGoToMainMenu();
    }

    private void anyInputGoToMainMenu() {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()) mainMenu();
    }

    private void mainMenu() {
        drawMainMenu();
        mainMenuChoice();
    }

    private void drawStartMenu() {
        System.out.println("【扫雷游戏  v0.3】");
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
                gameStart();
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

    private void gameStart() {
        System.out.println("【开始游戏】");
        System.out.println("本游戏版本目前支持初级难度（9*9，10雷）,中级难度（16*16，40雷），高级难度（16*30，99雷）和自定义模式");
        System.out.println("请输入数字来选择模式：1、初级 2、中级 3、高级 4、自定义 其他输入将视为退回主菜单");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();
        gameInit(select);
    }

    private void gameInit(int select) {
        int[] gameSettingInfo;//0存储行数，1存储列数，2存储地雷数
        switch (select) {
            case 1:
                System.out.println("你选择了初级难度...");
                gameSettingInfo = new int[]{9, 9, 10};
                break;
            case 2:
                System.out.println("你选择了中级难度...");
                gameSettingInfo = new int[]{16, 16, 40};
                break;
            case 3:
                System.out.println("你选择了高级难度...");
                gameSettingInfo = new int[]{16, 30, 99};
                break;
            case 4:
                System.out.println("你选择了自定义模式...");
                System.out.println("请输入你想要的行、列数和地雷数量：");
                System.out.println("（格式为：“9,9,9”,逗号为英文逗号，行数<=16，列数<=30,0<地雷数<=min(99,行*列-1））");
                System.out.println("(如果输入非法，将使用初级难度开始游戏)");
                gameSettingInfo = costumizedMode();
                break;
            default:
                mainMenu();
                return;
        }

        Gaming game = new Gaming(gameSettingInfo[0], gameSettingInfo[1], gameSettingInfo[2]);
        chooseDig(game);
    }

    private int[] costumizedMode() {

        Scanner scanner = new Scanner(System.in);
        String aInput = scanner.nextLine();
        String[] info = aInput.split(",");

        if (info.length == 3) {
            int[] gameInfo = new int[3];
            gameInfo[0] = Integer.parseInt(info[0]);
            gameInfo[1] = Integer.parseInt(info[1]);
            gameInfo[2] = Integer.parseInt(info[2]);
            if (gameInfo[0] <= 16 && gameInfo[0] > 0
                    && gameInfo[1] <= 30 && gameInfo[1] > 0
                    && gameInfo[2] <= 99 && gameInfo[2] <= gameInfo[0] * gameInfo[1] - 1 && gameInfo[2] > 0) {
                return gameInfo;
            }
        }
        return new int[]{9, 9, 10};

    }

    private void chooseDig(Gaming aGame) {
        System.out.println("请选择要挖开的区块（书写格式，如：“1,2”，1代表第一行，2代表第二列）：");
        System.out.println("（输入'0'就退出游戏）");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        if (input.equals("0")) System.exit(0);
        else {
            if (aGame.correctInput(input)) {

                if (!aGame.drawUserMineMapMask(input) && !aGame.isLoseFlag()) chooseDig(aGame);
                else {
                    if (aGame.isLoseFlag()) {
                        System.out.println("对不起，欢迎再来挑战！祝你下次成功！");
                    } else {
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
        System.out.println("v0.3:现在支持选择难度和自定义难度啦~");
        System.out.println("任意输入返回上一级菜单~");
        anyInputGoToMainMenu();
    }

}

class Gaming {
    private int col;
    private int row;
    private int[][] mineMap;
    private boolean[][] mineMapMask;
    private int[][] offset = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    private boolean loseFlag;
    private int mineNum;
    private int remainingUndig;

//    Gaming() {
//        row = 9;
//        col = 9;
//        loseFlag = false;
//        initMineMap(9, 9, 10);
//        mineMapMask = new boolean[11][11];
//        setBoundary(9, 9);
//        mineNum = 10;
//        remainingUndig = 81;
//    }

    Gaming(int row, int col, int mineNum) {
        this.row = row;
        this.col = col;
        loseFlag = false;
        initMineMap(row, col, mineNum);
        mineMapMask = new boolean[row + 2][col + 2];
        setBoundary(row, col);
        this.mineNum = mineNum;
        remainingUndig = row * col;
    }

    private void initMineMap(int row, int col, int mineNum) {
        System.out.println("正在生成雷图……");
        mineGenerator(row, col, mineNum);
        numGenerator(row, col);
        System.out.println("已生成雷图");
    }

    /**
     * 负责生成雷图
     * TODO:现在的算法效率在雷的数量较大时会很低，需要日后优化一下
     */
    private void mineGenerator(int aRow, int aCol, int mineNum) {
        mineMap = new int[aRow + 2][aCol + 2];
        boolean finishMineGeneratorFlag = true;
        Random rand = new Random();
        int mineCount = 0;
        while (finishMineGeneratorFlag) {
            int row = rand.nextInt(aRow) + 1;
            int col = rand.nextInt(aCol) + 1;
            if (mineMap[row][col] != 9) {
                mineMap[row][col] = 9;
                //System.out.println("哈哈，埋个地雷在[" + row + "," + col + "]！");//测试用语句
                mineCount++;
            }
            if (mineCount == mineNum) {
                finishMineGeneratorFlag = false;
            }
        }
    }

    /**
     * 负责生成显示周边地雷的数字
     */
    private void numGenerator(int row, int col) {

        for (int i = 1; i < row + 1; i++) {
            for (int j = 1; j < col + 1; j++) {
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

    private void setBoundary(int row, int col) {
        for (int i = 0; i <= row + 1; i++) {
            mineMapMask[i][0] = mineMapMask[i][col + 1] = true;
        }
        for (int i = 0; i <= col + 1; i++) {
            mineMapMask[0][i] = mineMapMask[row + 1][i] = true;
        }
    }

    boolean isLoseFlag() {
        return loseFlag;
    }

    boolean drawUserMineMapMask(String aInput) {
        String[] inputArray = aInput.split(",");
        int row = Integer.parseInt(inputArray[0]);//列
        int col = Integer.parseInt(inputArray[1]);//行
        if (!userMineMapMaskAfterDig(row, col)) {
            displayMineMap();
            return false;
        } else return true;
    }

    /**
     * 负责在每一次挖掘后生成用户视野掩码
     *
     * @param aRow 行数
     * @param aCol 列数
     * @return true，如果赢了；false，还没赢
     */
    private boolean userMineMapMaskAfterDig(int aRow, int aCol) {
        if (mineMapMask[aRow][aCol]) {
            System.out.println("你特么已经挖过[" + aRow + "，" + aCol + "]了！！！快重新选一个！");
        } else {
            if (mineMap[aRow][aCol] == 9) {
                loseFlag = true;
                dig(aRow, aCol);
                System.out.println("你输了，[" + aRow + "，" + aCol + "]是地雷");
            } else {
                dig(aRow, aCol);
            }
        }

        return checkWinOrNot();
    }

    private boolean checkWinOrNot() {
        return (remainingUndig == mineNum);
    }

    private void dig(int aRow, int aCol) {
        mineMapMask[aRow][aCol] = true;
        if (mineMap[aRow][aCol] != 9) remainingUndig--;
        if (mineMap[aRow][aCol] == 0) {
            for (int i = 0; i < 8; i++) {
                //System.out.println("正在Dig[" + (aRow + offset[i][0]) + "][" + (aCol + offset[i][1]) + "]");//测试用语句
                if (!mineMapMask[aRow + offset[i][0]][aCol + offset[i][1]])
                    dig(aRow + offset[i][0], aCol + offset[i][1]);
            }
        }

    }

    private void displayMineMap() {
        System.out.print("   ");
        for(int j = 1; j<=col;j++)System.out.printf(" %2d ", j);
        System.out.println();
        for (int i = 1; i <= row; i++) {
            System.out.printf("%2d ",i);
            for (int j = 1; j <= col; j++) {
                if (!mineMapMask[i][j]) {
                    System.out.print("[  ]");
                } else {
                    System.out.printf("[% 2d]", mineMap[i][j]);
                }
            }
            System.out.println();
        }
    }

    /**
     * 检查输入的合法性
     *
     * @param aInput 输入的字符串
     * @return true，当输入符合格式要求；false，当输入不符合要求
     */
    //TODO:现在的检查方法写得很屎，无法检查parseInt抛出的异常，需要修改
    boolean correctInput(String aInput) {
        String[] analyseInput = aInput.split(",");
        return (analyseInput.length == 2
                && Integer.parseInt(analyseInput[0]) <= row && Integer.parseInt(analyseInput[1]) <= col
                && Integer.parseInt(analyseInput[0]) > 0 && Integer.parseInt(analyseInput[1]) > 0);
    }
}