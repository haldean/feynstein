package feynstein.utilities;
import feynstein.geometry.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ObjParser {
	private final String VERTEX = "v";
	private final String FACE = "f";
	private final String TEXCOORD = "vt";
	private final String NORMAL = "vn";
	//private final String OBJECT = "o";
	private final String MATERIAL_LIB = "mtllib";
	private final String USE_MATERIAL = "usemtl";
	private final String NEW_MATERIAL = "newmtl";
	private final String DIFFUSE_TEX_MAP = "map_Kd";

	private HashMap<String, ObjMaterial> materialMap;
	private ArrayList<Vector3d> vertices;
	private ArrayList<Vector3d> normals;
	private ArrayList<Triangle> triangles;
	private String fileName;

	/**
	 * Creates a new OBJ parser instance
	 * 
	 */
	public ObjParser(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
		materialMap = new HashMap<String, ObjMaterial>();
		vertices = new ArrayList<Vector3d>();
		normals = new ArrayList<Vector3d>();
		triangles = new ArrayList<Triangle>();
	}

	public void parse() throws FileNotFoundException {
		long startTime = Calendar.getInstance().getTimeInMillis();
		
		BufferedReader buffer = new BufferedReader(
				new FileReader(fileName));
		String line;
		//co = new ParseObjectData(vertices, texCoords, normals);
		//parseObjects.add(co);

		//System.out.println("Start parsing object " + resourceID);
		System.out.println("Start time " + startTime);

		try {
			while ((line = buffer.readLine()) != null) {
				// remove duplicate whitespace
				// line = line.replaceAll("\\s+", " ");
				// String[] parts = line.split(" ");
				StringTokenizer parts = new StringTokenizer(line, " ");
				int numTokens = parts.countTokens();
				if (numTokens == 0)
					continue;
				String type = parts.nextToken();

				if (type.equals(VERTEX)) {
					//System.out.println("ADD Vertex: "+line);
					
					double x = Float.parseFloat(parts.nextToken());
					double y = Float.parseFloat(parts.nextToken());
					double z = Float.parseFloat(parts.nextToken());
					
					Vector3d vertex = new Vector3d(x,y,z);
					vertices.add(vertex);
				} else if (type.equals(FACE)) {
					if (numTokens == 4) {
						//System.out.println("ADD Face: "+line);
						//co.numFaces++;
						//co.faces.add(new ObjFace(line, currentMaterialKey, 3));
					} else if (numTokens == 5) {
						System.out.println("ADD Face 5: "+line);
						//co.numFaces += 2;
						//co.faces.add(new ObjFace(line, currentMaterialKey, 4));
					}
					parseTriangleFace(line, numTokens-1);
				} else if (type.equals(NORMAL)) {
					System.out.println("Store normal: "+line);
					double x = Float.parseFloat(parts.nextToken());
					double y = Float.parseFloat(parts.nextToken());
					double z = Float.parseFloat(parts.nextToken());
					Vector3d normal = new Vector3d(x,y,z);
					normals.add(normal);
				}  
				// MATERIALS SHIT WE WILL NEED LATER
				/*else if (type.equals(TEXCOORD)) {
					Uv texCoord = new Uv();
					texCoord.u = Float.parseFloat(parts.nextToken());
					texCoord.v = Float.parseFloat(parts.nextToken()) * -1f;
					texCoords.add(texCoord);
				}
				else if (type.equals(MATERIAL_LIB)) {
					readMaterialLib(parts.nextToken());
				} else if (type.equals(USE_MATERIAL)) {
					currentMaterialKey = parts.nextToken();
				}
				}*/
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		long endTime = Calendar.getInstance().getTimeInMillis();
		System.out.println("End time " + (endTime - startTime));
	}

	public Triangle parseTriangleFace(String line, int faceLength) {
		boolean emptyVt = line.indexOf("//") > -1;
		if(emptyVt) {
			line = line.replace("//", "/");
		}
		StringTokenizer parts = new StringTokenizer(line);
		parts.nextToken();
		StringTokenizer subParts = new StringTokenizer(parts.nextToken(), "/");
		int partLength = subParts.countTokens();
		boolean hasuv = partLength >= 2 && !emptyVt;
		boolean hasn = partLength == 3 || (partLength == 2 && emptyVt);
		
		int [] v = new int[faceLength];
		//if (hasuv)
		int [] uv = new int[faceLength];
		//if (hasn)
		int [] n = new int[faceLength];
		
		for (int i = 0; i < faceLength; i++) {
			if (i > 0)
				subParts = new StringTokenizer(parts.nextToken(), "/");
			
			int index = i;
			v[index] = (short) (Short.parseShort(subParts.nextToken()) - 1);
			if (hasuv) {
				uv[index] = (short) (Short.parseShort(subParts.nextToken()) - 1);
			}
			if (hasn) {
				n[index] = (short) (Short.parseShort(subParts.nextToken()) - 1);
			}
		}
		// TODO(sam): this method assumes that face length is 3. If there are more, we 
		// should handle them accordingly
		Triangle t = new Triangle(v);
		if(hasn)
			t.setNormals(normals.get(n[0]), normals.get(n[1]), normals.get(n[2]));
		return t;
	}
	
	/*private void readMaterialLib(String libID) {
		StringBuffer resourceID = new StringBuffer(packageID);
		StringBuffer libIDSbuf = new StringBuffer(libID);
		int dotIndex = libIDSbuf.lastIndexOf(".");
		if (dotIndex > -1)
			libIDSbuf = libIDSbuf.replace(dotIndex, dotIndex + 1, "_");

		resourceID.append(":raw/");
		resourceID.append(libIDSbuf.toString());

		InputStream fileIn = resources.openRawResource(resources.getIdentifier(
				resourceID.toString(), null, null));
		BufferedReader buffer = new BufferedReader(
				new InputStreamReader(fileIn));
		String line;
		String currentMaterial = "";

		try {
			while ((line = buffer.readLine()) != null) {
				String[] parts = line.split(" ");
				if (parts.length == 0)
					continue;
				String type = parts[0];

				if (type.equals(NEW_MATERIAL)) {
					if (parts.length > 1) {
						currentMaterial = parts[1];
						materialMap.put(currentMaterial, new ObjMaterial(
								currentMaterial));
					}
				} else if (type.equals(DIFFUSE_TEX_MAP)) {
					if (parts.length > 1) {
						materialMap.get(currentMaterial).diffuseTextureMap = parts[1];
						StringBuffer texture = new StringBuffer(packageID);
						texture.append(":drawable/");

						StringBuffer textureName = new StringBuffer(parts[1]);
						dotIndex = textureName.lastIndexOf(".");
						if (dotIndex > -1)
							texture.append(textureName.substring(0, dotIndex));
						else
							texture.append(textureName);

						int bmResourceID = resources.getIdentifier(texture
								.toString(), null, null);
						Bitmap b = Utils.makeBitmapFromResourceId(bmResourceID);
						textureAtlas.addBitmapAsset(new BitmapAsset(currentMaterial, texture.toString()));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 */
	
	private class ObjMaterial {
		public String name;
		public String diffuseTextureMap;
		public float offsetU;
		public float offsetV;

		public ObjMaterial(String name) {
			this.name = name;
		}
	}
}
