package ca.utoronto.utm.paint;
import java.io.*;

public class FileIO {
    /**
     * Write contents to fileName in users home directory.
     * @param contents
     * @param fileName
     * @return
     */
    public static boolean writeHomeFile(String contents, String fileName){
        String fullFileName = System.getProperty("user.home") + File.separator + fileName;
        return writeFile(contents, fullFileName);
    }

    /**
     * Write contents to fileName
     * @param contents
     * @param fileName
     * @return
     */
    private static boolean writeFile(String contents, String fileName){
        boolean retVal=true;
        PrintWriter out=null;
        try {
            out = new PrintWriter(fileName);
            out.println(contents);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { if(out!=null)out.close(); } catch (Exception e) {}
        }
        return retVal;
    }
    /**
     * Read and return the contents of fileName in users home directory.
     * @param fileName
     * @return
     */
    public static String readHomeFile(String fileName){
        String fullFileName = System.getProperty("user.home") + File.separator + fileName;
        return readFile(fullFileName);
    }
    /**
     * Read fileName and return its contents
     * @param fileName
     * @return the contents of the file as a String
     */
    private static String readFile(String fileName){
        StringBuilder contents = new StringBuilder();
        BufferedReader lineInput = null;
        try {
            lineInput = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = lineInput.readLine()) != null) {
                contents.append(line+'\n'); // Adds every line to response till the end of file.
            }
            lineInput.close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try { if(lineInput!=null)lineInput.close(); } catch (Exception e) {}
        }
        return contents.toString();
    }

    public static String readResourceFile(String fileName){
        String contents="";
        try {
            ClassLoader classLoader = Ollama.class.getClassLoader();
            String fullFileName = classLoader.getResource(fileName).getFile();
            // System.out.println(fullFileName);
            contents=readFile(fullFileName);
        } catch (Exception e){
            e.printStackTrace();
        }
        return contents;
    }
}
