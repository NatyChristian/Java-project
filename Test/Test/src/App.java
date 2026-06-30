import tester.Tester;


class App{
	String name;
	int s;
	App(String name, int s){
		this.name = name;
		this.s = s;
	}
}

class Examples{
	App a1 = new App("Mbola", 12);

	boolean testApp(Tester t){
		return t.checkExpect(a1.name, "Mbola");
	}

	public static void main(String[] args){
		Tester.runReport(new Examples(), false, true);
	}
}