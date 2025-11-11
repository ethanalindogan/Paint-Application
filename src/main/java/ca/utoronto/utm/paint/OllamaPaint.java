package ca.utoronto.utm.paint;

import java.io.*;
import java.util.regex.*;

public class OllamaPaint extends Ollama{
    public OllamaPaint(String host){
        super(host);
    }

    /**
     * Ask llama3 to generate a new Paint File based on the given prompt
     * @param prompt
     * @param outFileName name of new file to be created in users home directory
     */
    public void newFile(String prompt, String outFileName){
        // YOUR CODE GOES HERE
        String format = FileIO.readResourceFile("paintSaveFileFormat.txt");
        String system = "The answer to this question should be a Paint Save File. Respond only with a Paint Save File and nothing else. " + format;
        String response = this.call(system, prompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * Ask llama3 to generate a new Paint File based on a modification of inFileName and the prompt
     * @param prompt the user supplied prompt
     * @param inFileName the Paint File Format file to be read and modified to outFileName
     * @param outFileName name of new file to be created in users home directory
     */
    public void modifyFile(String prompt, String inFileName, String outFileName){
        String format = FileIO.readResourceFile("paintSaveFileFormat.txt");
        String system = "The answer to this question should be a Paint Save File. Respond only with a Paint Save File and nothing else. " + format;
        String fileContent = FileIO.readHomeFile(inFileName);
        String fullPrompt = "Modify the following Paint Save File: " + fileContent + "\nMODIFICATION: " + prompt;
        String response = this.call(system, fullPrompt);
        String extractedContent = clean(response);
        FileIO.writeHomeFile(extractedContent, outFileName);
    }

    /**
     * Extracts the content of the Paint Save File from the AI response.
     *
     * @param response The full response from the AI.
     * @return The extracted Paint Save File content, or null if not found.
     */
    private String clean(String response) {
        Pattern pattern = Pattern.compile("(Paint Save File Version 1\\.0.*?End Paint Save File)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            System.out.print(response);
            return null;
        }
    }


    /**
     * newFile1: Creates an artistic scene involving multiple trees of different sizes and colors.
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile1(String outFileName) {
        String prompt = "Create a scene with three trees, each having a different trunk height and leaf color. One tree should be tall and slender, another short and bushy, and the third medium-sized with colorful leaves.";
        newFile(prompt, outFileName);
    }

    /**
     * newFile2: Creates an imaginative scene involving a house with multiple shapes and decorative details.
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile2(String outFileName) {
        String prompt = "Draw a small house with a triangular roof, a rectangular door, and windows on each side, all in different colors and sizes.";
        newFile(prompt, outFileName);
    }

    /**
     * newFile3: Creates a vibrant scene with multiple overlapping and interconnected shapes.
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile3(String outFileName) {
        String prompt = "Draw a collection of interconnected and overlapping geometric shapes, including circles, triangles, and rectangles, each with a unique color. Make sure the shapes interact visually, creating an abstract pattern.";
        newFile(prompt, outFileName);
    }

    /**
     * modifyFile1: Modify inFileName to make all filled shapes unfilled.
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile1(String inFileName, String outFileName) {
        String prompt = "Make all filled shapes unfilled.";
        modifyFile(prompt, inFileName, outFileName);
    }

    /**
     * modifyFile2: Modify inFileName to change all colors to shades of blue.
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile2(String inFileName, String outFileName) {
        String prompt = "Change all colors to shades of blue.";
        modifyFile(prompt, inFileName, outFileName);
    }

    /**
     * modifyFile3: Modify inFileName to add a border rectangle around all shapes.
     * @param inFileName the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile3(String inFileName, String outFileName) {
        String prompt = "Add a border rectangle around all existing shapes.";
        modifyFile(prompt, inFileName, outFileName);
    }


    public static void main(String [] args){
        String prompt = null;

        prompt="Draw a 100 by 120 rectangle with 4 radius 5 circles at each rectangle corner.";
        OllamaPaint op = new OllamaPaint("dh2010pc03.utm.utoronto.ca"); // Replace this with your assigned Ollama server.

        prompt="Draw a 100 by 120 rectangle with 4 radius 5 circles at each rectangle corner.";
        op.newFile(prompt, "OllamaPaintFile1.txt");
        op.modifyFile("Remove all shapes except for the circles.","OllamaPaintFile1.txt", "OllamaPaintFile2.txt" );

        prompt="Draw 5 concentric circles with different colors.";
        op.newFile(prompt, "OllamaPaintFile3.txt");
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile3.txt", "OllamaPaintFile4.txt" );

        prompt="Draw a polyline then two circles then a rectangle then 3 polylines all with different colors.";
        op.newFile(prompt, "OllamaPaintFile4.txt");

        prompt="Modify the following Paint Save File so that each circle is surrounded by a non-filled rectangle. ";
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile4.txt", "OllamaPaintFile5.txt" );

        for(int i=1;i<=3;i++){
            op.newFile1("PaintFile1_"+i+".txt");
            op.newFile2("PaintFile2_"+i+".txt");
            op.newFile3("PaintFile3_"+i+".txt");
        }
        for(int i=1;i<=3;i++){
            for(int j=1;j<=3;j++) {
                op.modifyFile1("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_1.txt");
                op.modifyFile2("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_2.txt");
                op.modifyFile3("PaintFile"+ i +"_"+j+ ".txt", "PaintFile"+ i +"_"+j+"_3.txt");
            }
        }
    }
}
