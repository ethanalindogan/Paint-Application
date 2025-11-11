package ca.utoronto.utm.paint;

public class OllamaNumberedFile extends Ollama{
    public OllamaNumberedFile(String host){
        super(host);
    }

    /**
     * Ask llama3 to generate a new Numbered Document based on a modification of inFileName and the prompt
     * @param prompt the user supplied prompt
     * @param inFileName the Numbered File Format file to be read and modified to outFileName
     * @param outFileName the file name where the response will live
     */
    public void modifyFile(String prompt, String inFileName, String outFileName){
        String format = FileIO.readResourceFile("numberedDocumentFormat.txt");
        String system="The answer to this question should be a Numbered Document. Respond only with a Numbered Document and nothing else. " +format;
        String f = FileIO.readHomeFile(inFileName);
        String fullPrompt ="Produce a new Numbered Document, resulting from the following OPERATION being performed on the following Numbered Document. OPERATION START"+prompt+ " OPERATION END "+f;
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * Ask llama3 to generate a new Numbered Document based on the given prompt
     * @param prompt
     * @param outFileName
     */
    public void newFile(String prompt, String outFileName){
        String format = FileIO.readResourceFile("numberedDocumentFormat.txt");
        String system="The answer to this question should be a Numbered Document. Respond only with a Numbered Document and nothing else. " +format;
        String response = this.call(system, prompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    @Override
    public void newFile1(String outFileName) {}

    @Override
    public void newFile2(String outFileName) {}

    @Override
    public void newFile3(String outFileName) {}

    @Override
    public void modifyFile1(String inFileName, String outFileName) {}

    @Override
    public void modifyFile2(String inFileName, String outFileName) {}

    @Override
    public void modifyFile3(String inFileName, String outFileName) {}

    public static void main(String [] args){
        OllamaNumberedFile op = new OllamaNumberedFile("dh2010pc03.utm.utoronto.ca"); // replace with one of your servers
        op.newFile("Create a new Numbered File with 15 lines. The content of the lines should come from lines of The Canadian Charter of rights.", "numberedFileExample1.txt");
        op.modifyFile("Add a few more Numbered Lines to the given numbered file, the new lines should come from the US Constitution ","numberedFileExample1.txt", "numberedFileExample2.txt" );
    }
}
