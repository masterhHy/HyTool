package test;

public class TestConstrutor {
	int a;
	private TestConstrutor(String param1,String param2){
		
	}
	
	public TestConstrutor(String param1,String param2,int a){
		this(param1,param2);
		this.a=a;
		
	}
	public TestConstrutor(String param1,String param2,long a){
		this(param1,param2);
	}
	
}
