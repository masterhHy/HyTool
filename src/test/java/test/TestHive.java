package test;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestHive {

	public static void main(String[] args) throws Exception {
		 RandomAccessFile raf = new RandomAccessFile("C:/Users/Administrator/Desktop/js文件/aa.txt","r");
		 int i = raf.readInt();
		 System.out.println(i);
		 System.out.println(raf.length());
		 raf.close();
	}
	
	@Test
	public void test08(){
		List<TestHive> list = new ArrayList<>();
		TestHive testHive = new TestHive();
		TestHive testHive1 = new TestHive();
		TestHive testHive2 = new TestHive();
		list.add(testHive);
		list.add(testHive1);
		list.add(testHive2);
		for (TestHive val : list) {
			if(val.equals(testHive1)){
				list.remove(val);
			}
		}
		System.out.println(list);
	}
}
