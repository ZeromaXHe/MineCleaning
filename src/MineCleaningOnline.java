import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Scanner;

public class MineCleaningOnline {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(System.out,"UTF-8"),true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MapManagerOnline mm = new MapManagerOnline(out,in);
        mm.startMenu();
    }

    public void play(PrintWriter out, Scanner in){
        MapManagerOnline mm = new MapManagerOnline(out,in);
        mm.startMenu();
    }
}

class MapManagerOnline {
    private PrintWriter out;
    private Scanner in;

    MapManagerOnline(PrintWriter out, Scanner in){
        this.out = out;
        this.in = in;
    }

    void startMenu() {
        drawStartMenu();
        anyInputGoToMainMenu();
    }

    private void anyInputGoToMainMenu() {
        if (in.hasNextLine()) {
            in.nextLine();
            mainMenu();
        }
    }

    private void mainMenu() {
        drawMainMenu();
        mainMenuChoice();
    }

    private void drawStartMenu() {
        out.println("【扫雷游戏  v0.4】");
        out.println(" 开发者：朱肖訸  ");
        out.println("[任意输入开始游戏]");
    }

    private void drawMainMenu() {
        for(int i=0;i<5;i++)out.println();
        out.println("===============================================");
        out.println("【主菜单】");
        out.println("1、开始游戏  2、游戏设置  3、版本介绍  4、退出游戏");
    }

    private void mainMenuChoice() {
        out.println("请输入你的选择:");
        String select = in.nextLine();
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
                //TODO:System.exit(0);
                break;
            default:
                mainMenuChoice();
        }
    }

    private void gameStart() {
        for(int i=0;i<5;i++)out.println();
        out.println("【开始游戏】");
        out.println("本游戏版本目前支持初级难度（9*9，10雷）,中级难度（16*16，40雷），高级难度（16*30，99雷）和自定义模式");
        out.println("请输入数字来选择模式：1、初级 2、中级 3、高级 4、自定义 其他输入将视为退回主菜单");

        String select = in.nextLine();
        gameInit(select);
    }

    private void gameInit(String select) {
        int[] gameSettingInfo;//0存储行数，1存储列数，2存储地雷数
        switch (select) {
            case "1":
                out.println("你选择了初级难度...");
                gameSettingInfo = new int[]{9, 9, 10};
                break;
            case "2":
                out.println("你选择了中级难度...");
                gameSettingInfo = new int[]{16, 16, 40};
                break;
            case "3":
                out.println("你选择了高级难度...");
                gameSettingInfo = new int[]{16, 30, 99};
                break;
            case "4":
                out.println("你选择了自定义模式...");
                out.println("请输入你想要的行、列数和地雷数量：");
                out.println("（格式为：“9,9,9”,逗号为英文逗号，行数<=16，列数<=30,0<地雷数<=min(99,行*列-1））");
                out.println("(如果输入非法，将使用初级难度开始游戏)");
                gameSettingInfo = costumizedMode();
                break;
            default:
                mainMenu();
                return;
        }
        out.println("即将生成范围为："+gameSettingInfo[0]+"*"+gameSettingInfo[1]+"，地雷数量为："+gameSettingInfo[2]+"的游戏");//TODO:test, need to remove
        GamingOnline game = new GamingOnline(gameSettingInfo[0], gameSettingInfo[1], gameSettingInfo[2], out, in);
        chooseDig(game);
    }

    private int[] costumizedMode() {

        String aInput = in.nextLine();
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

    private void chooseDig(GamingOnline aGame) {
        out.println("请选择要挖开的区块（书写格式，如：“1,2”，1代表第一行，2代表第二列）：");
        out.println("（输入'0'就退出游戏）");

        String input = in.nextLine();
        if (input.equals("0")) return;//TODO:System.exit(0);
        else {
            if (aGame.correctInput(input)) {
                for(int i=0;i<5;i++)out.println();
                if (!aGame.drawUserMineMapMask(input) && !aGame.isLoseFlag())
                    chooseDig(aGame);
                else {
                    if (aGame.isLoseFlag()) {
                        out.println("对不起，欢迎再来挑战！祝你下次成功！");
                    } else {
                        out.println("你赢了，牛逼啊~");
                    }
                    out.println("[任意输入返回主菜单]");
                    anyInputGoToMainMenu();
                }
            } else {
                out.println("看清楚输入格式好吗？输入1~9的数字+英文半角逗号+1~9的数字，兄弟！");
                chooseDig(aGame);
            }
        }
    }

    private void optionMenu() {
        out.println("【游戏设置】");
        out.println("暂不支持本功能");
        out.println("任意输入返回上一级菜单~");
        anyInputGoToMainMenu();

    }

    private void versionMenu() {
        out.println("版本介绍：");
        out.println("v0.1:制作了基本的界面——开始菜单、主菜单等，实现了雷图的生成和用户视野掩码的制作");
        out.println("v0.2:制作了点中周围无地雷的地块时自动遍历周边地区的功能，实现了游戏失败、成功的判定，基本可以进行真正的游戏");
        out.println("v0.3:现在支持选择难度和自定义难度啦~");
        out.println("v0.4:现在支持开服务器让别人telnet连接服务器玩啦~");
        out.println("任意输入返回上一级菜单~");
        anyInputGoToMainMenu();
    }

}

class GamingOnline {
    private int col;
    private int row;
    private int[][] mineMap;
    private boolean[][] mineMapMask;
    private int[][] offset = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    private boolean loseFlag;
    private int mineNum;
    private int remainingUndig;
    private PrintWriter out;
    private Scanner in;

//    GamingOnline() {
//        row = 9;
//        col = 9;
//        loseFlag = false;
//        initMineMap(9, 9, 10);
//        mineMapMask = new boolean[11][11];
//        setBoundary(9, 9);
//        mineNum = 10;
//        remainingUndig = 81;
//    }

    GamingOnline(int row, int col, int mineNum, PrintWriter outter, Scanner inner) {
        this.out = outter;
        this.in = inner;
        this.row = row;
        this.col = col;
        loseFlag = false;
        mineMapMask = new boolean[row + 2][col + 2];
        this.mineNum = mineNum;
        remainingUndig = row * col;
        setBoundary(row, col);
        initMineMap(row, col, mineNum);

    }

    private void initMineMap(int row, int col, int mineNum) {
        out.println("正在生成雷图……");
        mineGenerator(row, col, mineNum);
        numGenerator(row, col);
        out.println("已生成雷图");
        displayMineMap();
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
        String[] inputArray = aInput.trim().split(",");
        int row = Integer.parseInt(inputArray[0]);//列
        int col = Integer.parseInt(inputArray[1]);//行
        if (!userMineMapMaskAfterDig(row, col)) {
            displayMineMap();
            return false;
        } else{
            displayMineMap();
            return true;
        }
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
            out.println("你特么已经挖过[" + aRow + "，" + aCol + "]了！！！快重新选一个！");
        } else {
            if (mineMap[aRow][aCol] == 9) {
                loseFlag = true;
                dig(aRow, aCol);
                out.println("!=!=!=!=!=!=!=!=!=!=!=!=!=!=!=!=!=!=!");
                out.println("你输了，[" + aRow + "，" + aCol + "]是地雷");
                out.println("!=!=!=!=!=!=!=!=!=!=!=!=!=!=!=!=!=!=!");
                out.println();
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
        out.print("   ");
        for(int j = 1; j<=col;j++)
            out.printf(" %2d ", j);
        out.println();
        for (int i = 1; i <= row; i++) {
            out.printf("%2d ",i);
            for (int j = 1; j <= col; j++) {
                if (!mineMapMask[i][j]) {
                    out.print("[  ]");
                } else {
                    out.printf("[% 2d]", mineMap[i][j]);
                }
            }
            out.println();
        }
    }

    /**
     * 检查输入的合法性
     *
     * @param aInput 输入的字符串
     * @return true，当输入符合格式要求；false，当输入不符合要求
     */

    boolean correctInput(String aInput){
        String[] analyseInput = aInput.trim().split(",");
        int n1;
        int n2;
        if(analyseInput.length == 2) {
            try{
                n1 = Integer.parseInt(analyseInput[0]);
                n2 = Integer.parseInt(analyseInput[1]);
            }
            catch (NumberFormatException e){
                return false;
            }
            if (n1 <= row && n2 <= col && n1 > 0 && n2 > 0) return true;
            else return false;
        }
        else return false;
    }
}