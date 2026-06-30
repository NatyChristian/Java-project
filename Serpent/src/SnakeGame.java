import java.util.Random;
import tester.Tester;
import java.awt.Color;
import javalib.funworld.*;
import javalib.worldimages.*;

class Fruit {
	int x, y;

	Fruit(int x, int y){
		this.x = x;
		this.y = y;
	}

	Fruit Gen(){
		Random rand = new Random();
		return new Fruit(rand.nextInt(45) + 1,rand.nextInt(45) + 1);
	}

	WorldScene drawOn(WorldScene scene){
		return scene.placeImageXY(
			new RectangleImage(32, 32, OutlineMode.SOLID, Color.RED),
			32 * ( this.x + 1/2) ,
			32 * ( this.y + 1/2)
		);
	}
}

class PieceOfSnake{
	int x, y;
	int PX = 32;

	PieceOfSnake(int x, int y){
		this.x = x;
		this.y = y;
	}
	PieceOfSnake moveLeft(){
	    return new PieceOfSnake((((this.x - 2) % 50 + 50) % 50) + 1, this.y);
	}
	PieceOfSnake moveRight(){
	    return new PieceOfSnake((this.x % 50) + 1, this.y);
	}
	PieceOfSnake moveUp(){
	    return new PieceOfSnake(this.x, (((this.y - 2) % 50 + 50) % 50) + 1);
	}
	PieceOfSnake moveDown(){
	    return new PieceOfSnake(this.x, (this.y % 50) + 1);
	}
	WorldScene drawOn(WorldScene scene){
		return scene.placeImageXY(
			new RectangleImage(this.PX, this.PX, OutlineMode.SOLID, Color.GREEN),
			this.PX * ( this.x + 1/2) ,
			this.PX * ( this.y + 1/2)
		);
	}

	boolean sameCord(Fruit fruit){
		return 	(Math.abs(this.x - fruit.x) * 32 <= 2) &&
				(Math.abs(this.y - fruit.y) * 32 <= 2);
	}
}

interface ILoPoSnake{
	ILoPoSnake moveUp();
	ILoPoSnake moveDown();
	ILoPoSnake moveLeft();
	ILoPoSnake moveRight();
	ILoPoSnake dropEnd();
	boolean isEmpty();
	WorldScene drawOn(WorldScene scene);
	PieceOfSnake getFirst();
}

class MtLoPoSnake implements ILoPoSnake{
	MtLoPoSnake(){}
	public ILoPoSnake moveUp(){
		return this;
	}
	public ILoPoSnake dropEnd(){
		return new MtLoPoSnake();
	}
	public boolean isEmpty(){
		return true;
	}
	public ILoPoSnake moveDown(){
		return this;
	}
	public ILoPoSnake moveLeft(){
		return this;
	}
	public ILoPoSnake moveRight(){
		return this;
	}
	public WorldScene drawOn(WorldScene scene){
		return scene;
	}
	public PieceOfSnake getFirst(){
		return new PieceOfSnake(-100, -100);
	}
}

class ConsLoPoSnake implements ILoPoSnake{
	PieceOfSnake first;
	ILoPoSnake rest;

	ConsLoPoSnake(PieceOfSnake first, ILoPoSnake rest){
		this.first = first;
		this.rest = rest;
	}
	public ILoPoSnake dropEnd(){
		if (rest.isEmpty()){
			return rest;
		} else {
			return new ConsLoPoSnake(this.first, this.rest.dropEnd());
		}
	}
	public boolean isEmpty(){
		return false;
	}
	public ILoPoSnake moveUp(){
		return new ConsLoPoSnake(this.first.moveUp(), this.dropEnd());
	}
	public ILoPoSnake moveDown(){
		return new ConsLoPoSnake(this.first.moveDown(), this.dropEnd());
	}
	public ILoPoSnake moveLeft(){
		return new ConsLoPoSnake(this.first.moveLeft(), this.dropEnd());
	}
	public ILoPoSnake moveRight(){
		return new ConsLoPoSnake(this.first.moveRight(), this.dropEnd());
	}
	public WorldScene drawOn(WorldScene scene){
		return this.rest.drawOn(this.first.drawOn(scene));
	}
	public PieceOfSnake getFirst(){
		return this.first;
	}
}



class Examples{
	PieceOfSnake p1 = new PieceOfSnake(1, 1);
	PieceOfSnake p2 = new PieceOfSnake(1, 2);
	PieceOfSnake p3 = new PieceOfSnake(2, 2);
	PieceOfSnake p4 = new PieceOfSnake(3, 2);
	PieceOfSnake p5 = new PieceOfSnake(3, 3);

	ILoPoSnake l1 = new ConsLoPoSnake(p1, new MtLoPoSnake());
	ILoPoSnake l2 = new ConsLoPoSnake(p2, l1);
	ILoPoSnake l3 = new ConsLoPoSnake(p3, l2);
	ILoPoSnake l4 = new ConsLoPoSnake(p4, l3);
	ILoPoSnake l5 = new ConsLoPoSnake(p5, l4);

	void testMoves(Tester t){
			t.checkExpect(this.p3.moveLeft(),  new PieceOfSnake(1, 2));
			t.checkExpect(this.p3.moveRight(), new PieceOfSnake(3, 2));
			t.checkExpect(this.p3.moveDown(), new PieceOfSnake(2, 3));
			t.checkExpect(this.p3.moveUp(), new PieceOfSnake(2, 1));
		}
	
	void testDropEnd(Tester t){
		t.checkExpect(l1.dropEnd(), new MtLoPoSnake());
		t.checkExpect(l2.dropEnd(), new ConsLoPoSnake(p2, new MtLoPoSnake()));
		t.checkExpect(
			new ConsLoPoSnake(p5, l5.dropEnd()),
			new ConsLoPoSnake(
				p5,
				new ConsLoPoSnake(
					p5, new ConsLoPoSnake(
						p4, new ConsLoPoSnake(
							p3, new ConsLoPoSnake(
								p2, new MtLoPoSnake()))))
							)
						);
	}

	void testMoveUp(Tester t){
		t.checkExpect(
			l2.moveUp(),
			new ConsLoPoSnake(p2.moveUp(), l2.dropEnd())
		);
		t.checkExpect(
			l5.moveUp(),
			new ConsLoPoSnake(p5.moveUp(), l5.dropEnd())
		);
	}
	void testDown(Tester t){
		t.checkExpect(
			l2.moveDown(),
			new ConsLoPoSnake(p2.moveDown(), l2.dropEnd())
		);
		t.checkExpect(
			l5.moveDown(),
			new ConsLoPoSnake(p5.moveDown(), l5.dropEnd())
		);
	}
	void testLeft(Tester t){
		t.checkExpect(
			l2.moveLeft(),
			new ConsLoPoSnake(p2.moveLeft(), l2.dropEnd())
		);
		t.checkExpect(
			l5.moveLeft(),
			new ConsLoPoSnake(p5.moveLeft(), l5.dropEnd())
		);
	}
	void testRight(Tester t){
		t.checkExpect(
			l2.moveRight(),
			new ConsLoPoSnake(p2.moveRight(), l2.dropEnd())
		);
		t.checkExpect(
			l5.moveRight(),
			new ConsLoPoSnake(p5.moveRight(), l5.dropEnd())
		);
	}
}

// !!!!!!
class SnakeGame extends World{

	int WIDTH = (new PieceOfSnake(0, 0)).PX * 50;
	int HEIGHT = (new PieceOfSnake(0, 0)).PX * 50;
	ILoPoSnake snake;
	String dir;
	Fruit fruit;
	int score;

	SnakeGame(ILoPoSnake snake, String dir, Fruit fruit, int score){
		super();
		this.snake = snake;
		this.dir =  dir;
		this.fruit = fruit;
		this.score = score;
	}

	public World onTick(){
		if (this.dir == "D") {
			if (this.snake.moveDown().getFirst().sameCord(this.fruit)) {
				this.score = this.score + 1;
				return new SnakeGame(
					new ConsLoPoSnake(
						new PieceOfSnake(this.fruit.x, this.fruit.y), 
						this.snake),
						"D",
						this.fruit.Gen(),
						this.score
				);
			} else {
				return new SnakeGame(this.snake.moveDown(), "D", this.fruit, this.score);
			}
		} else if (this.dir == "U") {
			if (this.snake.moveUp().getFirst().sameCord(this.fruit)) {
				this.score = this.score + 1;
				return new SnakeGame(
					new ConsLoPoSnake(
						new PieceOfSnake(this.fruit.x, this.fruit.y), 
						this.snake),
						"U",
						this.fruit.Gen(),
						this.score
				);
			} else {
				return new SnakeGame(this.snake.moveUp(), "U", this.fruit, this.score);
			}
		} else if (this.dir == "L") {
			if (this.snake.moveLeft().getFirst().sameCord(this.fruit)) {
				this.score = this.score + 1;
				return new SnakeGame(
					new ConsLoPoSnake(
						new PieceOfSnake(this.fruit.x, this.fruit.y), 
						this.snake),
						"L",
						this.fruit.Gen(),
						this.score
				);
			} else {
				return new SnakeGame(this.snake.moveLeft(), "L", this.fruit, this.score);
			}
		} else if (this.dir == "R") {
			if (this.snake.moveRight().getFirst().sameCord(this.fruit)) {
				this.score = this.score + 1;
				return new SnakeGame(
					new ConsLoPoSnake(
						new PieceOfSnake(this.fruit.x, this.fruit.y), 
						this.snake),
						"R",
						this.fruit.Gen(),
						this.score
				);
			} else {
				return new SnakeGame(this.snake.moveRight(), "R", this.fruit, this.score);
			}
		} else {
			return this;
		}
	}

	public World onKeyEvent(String key){
		if (key.equals("up")) {
			if (this.dir != "D") {
				if (!(this.snake.moveUp().getFirst().sameCord(fruit))) {
					return new SnakeGame(this.snake.moveUp(), "U", this.fruit, this.score);
				} else {
					return new SnakeGame(
					new ConsLoPoSnake(
						new PieceOfSnake(this.fruit.x, this.fruit.y), 
						this.snake),
						"U",
						this.fruit.Gen(),
						this.score + 1
				);
				}
			} else {
				return this;
			}
		} else if (key.equals("down")) {
			if (this.dir != "U") {
				if (!(this.snake.moveDown().getFirst().sameCord(fruit))) {
					return new SnakeGame(this.snake.moveDown(), "D", this.fruit, this.score);
				} else {
					return new SnakeGame(
					new ConsLoPoSnake(
						new PieceOfSnake(this.fruit.x, this.fruit.y), 
						this.snake),
						"D",
						this.fruit.Gen(),
						this.score + 1
				);
				}
			} else {
				return this;
			}
		} else if (key.equals("left")) {
			if (this.dir != "R") {
				if (!(this.snake.moveLeft().getFirst().sameCord(fruit))) {
					return new SnakeGame(this.snake.moveLeft(), "L", this.fruit, this.score);
				} else {
					return new SnakeGame(
					new ConsLoPoSnake(
						new PieceOfSnake(this.fruit.x, this.fruit.y), 
						this.snake),
						"L",
						this.fruit.Gen(),
						this.score + 1
				);
				}
			} else {
				return this;
			}
		} else if (key.equals("right")) {
			if (this.dir != "L") {
				if (!(this.snake.moveRight()).getFirst().sameCord(fruit)) {
					return new SnakeGame(this.snake.moveRight(), "R", this.fruit, this.score);
				} else {
					return new SnakeGame(
					new ConsLoPoSnake(
						new PieceOfSnake(this.fruit.x, this.fruit.y), 
						this.snake),
						"R",
						this.fruit.Gen(),
						this.score + 1
				);
				}
			} else {
				return this;
			}
		} else {
			return this;
		}
	}

	public WorldScene makeScene(){
		WorldScene scene0 = new WorldScene(this.WIDTH, this.HEIGHT);
		WorldScene scene1 = this.snake.drawOn(scene0);
		WorldScene scene2 = this.fruit.drawOn(scene1);
		WorldImage scores =  new TextImage("Scores:" + String.valueOf(this.score), Color.BLACK);
		return scene2.placeImageXY(scores, 35, 20);
	}
	public static void main(String[] args){
		Tester.runReport(new Examples(), false, false);
		SnakeGame game = new SnakeGame((new Examples()).l5, "D", new Fruit(30, 30), 0);
		game.bigBang(game.WIDTH, game.HEIGHT, 0.1);
	}
}