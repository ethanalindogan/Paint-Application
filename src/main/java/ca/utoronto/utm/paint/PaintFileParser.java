package ca.utoronto.utm.paint;

import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * 
 * @author 
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private PaintModel paintModel;

	/**
	 * Below are Patterns used in parsing
	 */
	private Pattern pFileStart = Pattern.compile("^Paint\\s*Save\\s*File\\s*Version\\s*1\\.0$");
	private Pattern pFileEnd = Pattern.compile("^End\\s*Paint\\s*Save\\s*File$");
	private Pattern pEmpty = Pattern.compile("");

	private Pattern pCircleStart = Pattern.compile("^Circle$");
	private Pattern pCircleEnd = Pattern.compile("^End\\s*Circle$");
	private Pattern pCircleColor = Pattern.compile("^\\s*color\\s*:\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)$");
	private Pattern pCircleFill = Pattern.compile("^\\s*filled\\s*:\\s*(true|false)$");
	private Pattern pCircleCentre = Pattern.compile("^\\s*center\\s*:\\s*\\((-?\\d+)\\s*,\\s*(-?\\d+)\\)$");
	private Pattern pCircleRadius = Pattern.compile("^\\s*radius\\s*:\\s*(-?\\d+)$");

	private Pattern pRectangleStart = Pattern.compile("^Rectangle$");
	private Pattern pRectangleEnd = Pattern.compile("^End\\s*Rectangle$");
	private Pattern pRectangleColor = Pattern.compile("^\\s*color\\s*:\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)$");
	private Pattern pRectangleFill = Pattern.compile("^\\s*filled\\s*:\\s*(true|false)$");
	private Pattern pRectangleP1 = Pattern.compile("^\\s*p1\\s*:\\s*\\((-?\\d+)\\s*,\\s*(-?\\d+)\\)$");
	private Pattern pRectangleP2 = Pattern.compile("^\\s*p2\\s*:\\s*\\((-?\\d+)\\s*,\\s*(-?\\d+)\\)$");

	private Pattern pSquiggleStart = Pattern.compile("^Squiggle$");
	private Pattern pSquiggleEnd = Pattern.compile("^End\\s*Squiggle$");
	private Pattern pSquiggleColor = Pattern.compile("^\\s*color\\s*:\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)$");
	private Pattern pSquiggleFill = Pattern.compile("^\\s*filled\\s*:\\s*(true|false)$");
	private Pattern pSquigglePointsStart = Pattern.compile("^\\s*points$");
	private Pattern pSquigglePoints = Pattern.compile("^\\s*point\\s*:\\s*\\((-?\\d+)\\s*,\\s*(-?\\d+)\\)$");
	private Pattern pPointsEnd = Pattern.compile("^\\s*end\\s*points$");

	private Pattern pPolylineStart = Pattern.compile("^Polyline$");
	private Pattern pPolylineEnd = Pattern.compile("^End\\s*Polyline$");
	private Pattern pPolylineColor = Pattern.compile("^\\s*color\\s*:\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)$");
	private Pattern pPolylineFill = Pattern.compile("^\\s*filled\\s*:\\s*(true|false)$");
	private Pattern pPolylinePointsStart = Pattern.compile("^\\s*points$");
	private Pattern pPolylinePoints = Pattern.compile("^\\s*point\\s*:\\s*\\((-?\\d+)\\s*,\\s*(-?\\d+)\\)$");


	// ADD MORE!!

	/**
	 * Store an appropriate error message in this, including
	 * lineNumber where the error occurred.
	 * @param mesg
	 */
	private void error(String mesg){
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
	}

	/**
	 *
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}

	/**
	 * Parse the specified file
	 * @param fileName
	 * @return
	 */
	public boolean parse(String fileName){
		boolean retVal = false;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			PaintModel pm = new PaintModel();
			retVal = this.parse(br, pm);
		} catch (FileNotFoundException e) {
			error("File Not Found: "+fileName);
		} finally {
			try { br.close(); } catch (Exception e){};
		}
		return retVal;
	}

	/**
	 * Parse the specified inputStream as a Paint Save File Format file.
	 * @param inputStream
	 * @return
	 */
	public boolean parse(BufferedReader inputStream){
		PaintModel pm = new PaintModel();
		return this.parse(inputStream, pm);
	}

	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 *
	 * @param inputStream the open file to parse
	 * @param paintModel the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
		this.paintModel = paintModel;
		this.errorMessage="";

		// During the parse, we will be building one of the
		// following commands. As we parse the file, we modify
		// the appropriate command.

		CircleCommand circleCommand = null;
		RectangleCommand rectangleCommand = null;
		SquiggleCommand squiggleCommand = null;
		PolylineCommand polylineCommand = null;
		try {
			int state=0; Matcher m; String l;

			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				l = l.replaceAll("\\s+", "");
				this.lineNumber++;
				System.out.println(lineNumber+" "+l+" "+state);
				switch(state){
					case 0:
						m = pFileStart.matcher(l);
						if (m.matches()) {
							state = 1;
							break;
						}
						error("Expected Start of Paint Save File");
						return false;
					case 1: // Looking for the start of a new object or end of the save file
						m = pCircleStart.matcher(l);
						Point p = new Point(1, 1);
						if (m.matches()) {
							circleCommand = new CircleCommand(p, 1);
							state = 2;
							break;
						}
						m = pRectangleStart.matcher(l);
						if (m.matches()) {
							rectangleCommand = new RectangleCommand(p, p);
							state = 7;
							break;
						}
						m = pSquiggleStart.matcher(l);
						if (m.matches()) {
							squiggleCommand = new SquiggleCommand();
							state = 12;
							break;
						}
						m = pPolylineStart.matcher(l);
						if (m.matches()) {
							polylineCommand = new PolylineCommand();
							state = 17;
							break;
						}
						m = pFileEnd.matcher(l);
						if (m.matches()) {
							state = 22;
							break;
						}
						m = pEmpty.matcher(l);
						if (m.matches()) {
							state = 1;
							break;
						}

						error("Expected Start of Shape or End Paint Save File");
						return false;
					case 2:
						m = pCircleColor.matcher(l);
						if (m.matches()) {
							try {
								int r = Integer.parseInt(m.group(1));
								int g = Integer.parseInt(m.group(2));
								int b = Integer.parseInt(m.group(3));
								if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
									error("Color values must be between 0 and 255");
									return false;}
								circleCommand.setColor(Color.rgb(Integer.parseInt(m.group(1)),
										Integer.parseInt(m.group(2)),
										Integer.parseInt(m.group(3))));
								state = 3;
								break;
							} catch (Exception e) {
								error("Expected Circle color");
								return false;
							}
						}
						error("Expected Circle color");
						return false;
					case 3:
						m = pCircleFill.matcher(l);
						if (m.matches()) {
							try {
								circleCommand.setFill(Boolean.parseBoolean(m.group(1)));
								state = 4;
								break;
							} catch (Exception e) {
								error("Expected Circle filled");
								return false;
							}
						}
						error("Expected Circle filled");
						return false;
					case 4:
						m = pCircleCentre.matcher(l);
						if (m.matches()) {
							try {
								circleCommand.setCentre(new Point(Integer.parseInt(m.group(1)),
										Integer.parseInt(m.group(2))));
								state = 5;
								break;
							} catch (Exception e) {
								error("Expected Circle center");
								return false;
							}
						}
						error("Expected Circle center");
						return false;
					case 5:
						m = pCircleRadius.matcher(l);
						if (m.matches()) {
							try {
								circleCommand.setRadius(Integer.parseInt(m.group(1)));
								state = 6;
								break;
							} catch (Exception e) {
								error("Expected Circle radius");
								return false;
							}
						}
						error("Expected Circle radius");
						return false;
					case 6:
						m = pCircleEnd.matcher(l);
						if (m.matches()) {
							paintModel.addCommand(circleCommand);
							circleCommand = null;
							state = 1;
							break;
						}
						error("Expected End Circle");
						return false;
					case 7:
						m = pRectangleColor.matcher(l);
						if (m.matches()) {
							try {
								int r = Integer.parseInt(m.group(1));
								int g = Integer.parseInt(m.group(2));
								int b = Integer.parseInt(m.group(3));
								if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
									error("Color values must be 0 - 255");
									return false;}
								rectangleCommand.setColor(Color.rgb(Integer.parseInt(m.group(1)),
										Integer.parseInt(m.group(2)),
										Integer.parseInt(m.group(3))));
								state = 8;
								break;
							} catch (Exception e) {
								error("Expected Rectangle color");
								return false;
							}
						}
						error("Expected Rectangle color");
						return false;
					case 8:
						m = pRectangleFill.matcher(l);
						if (m.matches()) {
							try {
								rectangleCommand.setFill(Boolean.parseBoolean(m.group(1)));
								state = 9;
								break;
							} catch (Exception e) {
								error("Expected Rectangle filled");
								return false;
							}
						}
						error("Expected Rectangle filled");
						return false;
					case 9:
						m = pRectangleP1.matcher(l);
						if (m.matches()) {
							try {
								rectangleCommand.setP1(new Point(Integer.parseInt(m.group(1)),
										Integer.parseInt(m.group(2))));
								state = 10;
								break;
							} catch (Exception e) {
								error("Expected Rectangle p1");
								return false;
							}
						}
						error("Expected Rectangle p1");
						return false;
					case 10:
						m = pRectangleP2.matcher(l);
						if (m.matches()) {
							try {
								rectangleCommand.setP2(new Point(Integer.parseInt(m.group(1)),
										Integer.parseInt(m.group(2))));
								state = 11;
								break;
							} catch (Exception e) {
								error("Expected Rectangle p2");
								return false;
							}
						}
						error("Expected Rectangle p2");
						return false;
					case 11:
						m = pRectangleEnd.matcher(l);
						if (m.matches()) {
							paintModel.addCommand(rectangleCommand);
							rectangleCommand = null;
							state = 1;
							break;
						}
						error("Expected End Rectangle");
						return false;
					case 12:
						m = pSquiggleColor.matcher(l);
						if (m.matches()) {
							try {
								int r = Integer.parseInt(m.group(1));
								int g = Integer.parseInt(m.group(2));
								int b = Integer.parseInt(m.group(3));
								if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
									error("Color values must be 0 - 255");
									return false;}
								squiggleCommand.setColor(Color.rgb(Integer.parseInt(m.group(1)),
										Integer.parseInt(m.group(2)),
										Integer.parseInt(m.group(3))));
								state = 13;
								break;
							} catch (Exception e) {
								error("Expected Squiggle color");
								return false;
							}
						}
						error("Expected Squiggle color");
						return false;
					case 13:
						m = pSquiggleFill.matcher(l);
						if (m.matches()) {
							try {
								squiggleCommand.setFill(Boolean.parseBoolean(m.group(1)));
								state = 14;
								break;
							} catch (Exception e) {
								error("Expected Squiggle filled");
								return false;
							}
						}
						error("Expected Squiggle filled");
						return false;
					case 14:
						m = pSquigglePointsStart.matcher(l);
						if (m.matches()) {
							state = 15;
							break;
						}
						error("Expected Squiggle points");
						return false;
					case 15:
						m = pSquigglePoints.matcher(l);
						if (m.matches()) {
							try {
								squiggleCommand.add(new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
								break;
							} catch (Exception e) {
								error("Expected Squiggle point or end points");
							}
						} else if (pPointsEnd.matcher(l).matches()) {
							state = 16;
							break;
						}
						error("Expected Squiggle point or end points");
						return false;
					case 16:
						m = pSquiggleEnd.matcher(l);
						if (m.matches()) {
							paintModel.addCommand(squiggleCommand);
							squiggleCommand = null;
							state = 1;
							break;
						}
						error("Expected End Squiggle");
						return false;
					case 17:
						m = pPolylineColor.matcher(l);
						if (m.matches()) {
							try {
								int r = Integer.parseInt(m.group(1));
								int g = Integer.parseInt(m.group(2));
								int b = Integer.parseInt(m.group(3));
								if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
									error("Color values must be between 0 - 255");
									return false;}
								polylineCommand.setColor(Color.rgb(Integer.parseInt(m.group(1)),
										Integer.parseInt(m.group(2)),
										Integer.parseInt(m.group(3))));
								state = 18;
								break;
							} catch (Exception e) {
								error("Expected Polyline color");
								return false;
							}
						}
						error("Expected Polyline color");
						return false;
					case 18:
						m = pPolylineFill.matcher(l);
						if (m.matches()) {
							try {
								polylineCommand.setFill(Boolean.parseBoolean(m.group(1)));
								state = 19;
								break;
							} catch (Exception e) {
								error("Expected Polyline filled");
							}
						}
						error("Expected Polyline filled");
						return false;
					case 19:
						m = pPolylinePointsStart.matcher(l);
						if (m.matches()) {
							state = 20;
							break;
						}
						error("Expected Polyline points");
						return false;
					case 20:
						m = pPolylinePoints.matcher(l);
						if (m.matches()) {
							try {
								polylineCommand.add(new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
								break;
							} catch (Exception e) {
								error("Expected Polyline point or end points");
							}
						} else if (pPointsEnd.matcher(l).matches()) {
							state = 21;
							break;
						}
						error("Expected Polyline point or end points");
						return false;
					case 21:
						m = pPolylineEnd.matcher(l);
						if (m.matches()) {
							paintModel.addCommand(polylineCommand);
							polylineCommand = null;
							state = 1;
							break;
						}
						error("Expected End Polyline");
						return false;
					case 22:
						if (l.trim().isEmpty()) {
							break;
						}
						error("Extra content after End of File");
						return false;

					/**
					 * I have around 20+/-5 cases in my FSM. If you have too many
					 * more or less, you are doing something wrong. Too few, and I bet I can find
					 * a bad file that you will say is good. Too many and you are not capturing the right concepts.
					 *
					 * Here are the errors I catch. All of these should be in your code.
					 *
					 	error("Expected Start of Paint Save File");
						error("Expected Start of Shape or End Paint Save File");
						error("Expected Circle color");
						error("Expected Circle filled");
						error("Expected Circle center");
						error("Expected Circle Radius");
						error("Expected End Circle");
						error("Expected Rectangle color");
						error("Expected Rectangle filled");
						error("Expected Rectangle p1");
						error("Expected Rectangle p2");
						error("Expected End Rectangle");
						error("Expected Squiggle color");
						error("Expected Squiggle filled");
						error("Expected Squiggle points");
						error("Expected Squiggle point or end points");
						error("Expected End Squiggle");
						error("Expected Polyline color");
						error("Expected Polyline filled");
						error("Expected Polyline points");
						error("Expected Polyline point or end points");
						error("Expected End Polyline");
						error("Extra content after End of File");
						error("Unexpected end of file");
					 */
				}
			}
		}  catch (Exception e){

		}
		return true;
	}
}
