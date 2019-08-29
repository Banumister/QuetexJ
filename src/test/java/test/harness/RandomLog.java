/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package test.harness;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Random log message generator.
 */
final class RandomLog {

    private static final Logger LOGGER = Logger.getGlobal();
    private static final Random RND;
    private static final String[] TBL_PARA = new String[1024];

    static{
        long seed = 135792468; //System.nanoTime();
        RND = new Random(seed);

        StringBuilder buf = new StringBuilder();

        for(int ct = 0; ct < TBL_PARA.length; ct++){
            randomParagraph(buf);
            TBL_PARA[ct] = buf.toString();
            buf.setLength(0);
        }
    }


    /**
     * Hidden constructor.
     */
    private RandomLog(){
        assert false;
    }


    /**
     * create ranged random-int.
     *
     * @param from lower limit
     * @param to upper limit
     * @return random int number
     */
    private static int nextInt(int from, int to){
        int range = to - from + 1;
        int iVal = RND.nextInt(range);
        iVal += from;
        return iVal;
    }

    /**
     * create ranged random-int with mean of samplings.
     *
     * @param from lower limit
     * @param to upper limit
     * @param samples samples
     * @return random int number
     */
    private static int nextInt(int from, int to, int samples){
        int iVal = 0;
        for(int ct = 0; ct < samples; ct++){
            iVal += nextInt(from, to);
        }

        int result = iVal / samples;

        return result;
    }

    /**
     * random char generator.
     * small alphabet(a-z) only.
     *
     * @return random small capitol.
     */
    private static char randomChar(){
        int iVal = nextInt('a', 'z');
        char result = (char)iVal;
        return result;
    }

    /**
     * random word generator.
     * 3 to 10 char length word.
     *
     * @param txt random word result
     */
    private static void randomWord(StringBuilder txt){
        int len = nextInt(3, 10, 2);
        for(int ct = 0; ct < len; ct++){
            txt.append(randomChar());
        }
    }

    /**
     * random sentence generator.
     * 3 to 10 words sentence.
     * sentence ends with period.
     *
     * @param txt random sentence result
     */
    private static void randomSentence(StringBuilder txt){
        char top = randomChar();
        top -= 'a' - 'A';
        txt.append(top);

        int len = nextInt(3, 10, 2);
        boolean first = true;
        for(int ct = 0; ct < len; ct++){
            if(first){
                first = false;
            }else{
                txt.append(' ');
            }
            randomWord(txt);
        }
        txt.append('.');
    }

    /**
     * random paragraph generator.
     * 1 to 8 sentences paragraph.
     * paragraph ends with newline.
     *
     * @param txt random paragraph result
     */
    private static void randomParagraph(StringBuilder txt){
        int len = nextInt(1, 8);
        boolean first = true;
        for(int ct = 0; ct < len; ct++){
            if(first){
                first = false;
            }else{
                txt.append(' ');
            }
            randomSentence(txt);
        }
    }

    /**
     * get random message with random words.
     *
     * @return random message
     */
    static String getRandomMessage(){
        int idx = nextInt(0, TBL_PARA.length - 1);
        String result = TBL_PARA[idx];
        return result;
    }

    /**
     * Logging random message.
     */
    static void putRandomLog(){
        LOGGER.info(getRandomMessage());
        return;
    }

}
