package de.fruitfly.vr;

import static org.lwjgl.opengl.GL11.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Model {
	private String name;
	private List<Triangle> triangles = new LinkedList<Triangle>();
	private Texture texture;
	
	public Model(String name) {
		this.name = name;
		try {
			this.parse(new DataInputStream(this.getClass().getResourceAsStream(name + ".obj")));
			this.texture = TextureLoader.getTexture(null, this.getClass().getResourceAsStream(name + ".png"), true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void parse(DataInputStream stream) throws IOException  {
		List<Vector3f> vertices = new LinkedList<Vector3f>();
		List<Vector2f> textureCoords = new LinkedList<Vector2f>();
		
		while (true) {
			stream.mark(0);
			String line = stream.readLine();
			if (line == null) break;
			
			if (line.startsWith("v ")) {
				String[] tokens = line.replace("v ", "").split(" ");
				Vector3f point = new Vector3f();
				point.x = Float.parseFloat(tokens[0]);
				point.y = Float.parseFloat(tokens[1]);
				point.z = Float.parseFloat(tokens[2]);
				vertices.add(point);
			}
			else if (line.startsWith("vt ")) {
				String[] tokens = line.replace("vt ", "").split(" ");
				Vector2f point = new Vector2f();
				point.x = Float.parseFloat(tokens[0]);
				point.y = Float.parseFloat(tokens[1]);
				textureCoords.add(point);
			}
			else if (line.startsWith("f ")) {
				String[] tokens = line.replace("f ", "").split(" ");
				
				Triangle p1 = new Triangle();
				Triangle p2 = new Triangle();
				int vertexIndex = 0;
				int texCoordIndex = 0;
				for (int i=0; i<tokens.length; i++) {
					String[] comps = tokens[i].split("/");
					vertexIndex = Integer.parseInt(comps[0]) - 1;
					texCoordIndex = Integer.parseInt(comps[1]) - 1;
					
					if (i<3) {
						p1.vertices[i] = vertices.get(vertexIndex);
						p1.textureCoords[i] = textureCoords.get(texCoordIndex);
					}
				}
				
				triangles.add(p1);

				
				if (tokens.length == 4) {

					p2.vertices[0] = p1.vertices[2];
					p2.textureCoords[0] = p1.textureCoords[2];
					
					p2.vertices[1] = p1.vertices[1];
					p2.textureCoords[1] = p1.textureCoords[1];
					
					p2.vertices[2] = vertices.get(vertexIndex);
					p2.textureCoords[2] = textureCoords.get(texCoordIndex);
					
					triangles.add(p2);
				}
			}
		}
	}
	Random r = new Random();
	public void render() {
		this.texture.bind();
		glBegin(GL_TRIANGLES);
			for (Triangle t : this.triangles) {
				//glColor3f(r.nextFloat(), r.nextFloat(), r.nextFloat());

				for (int i=0; i<3; i++) {
					Vector3f p = t.vertices[i];
					Vector2f tex = t.textureCoords[i];
					glTexCoord2f(tex.x, tex.y);
					glVertex3f(p.x, p.y, p.z);
				}
			}
		glEnd();
	}
}
