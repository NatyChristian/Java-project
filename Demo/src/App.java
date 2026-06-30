import javalib.worldimages.*;
import javalib.impworld.*;
import java.awt.Color;


class App {
	public static void main(String[] args){
		WorldImage cirlce = new CircleImage(10, OutlineMode.SOLID, Color.RED);

		WorldScene scene = new WorldScene(20, 20);

		scene.placeImageXY(cirlce, 10, 10);

		scene.saveImage("Test.png");
		System.out.println("The file have been created successfully");
	}
}