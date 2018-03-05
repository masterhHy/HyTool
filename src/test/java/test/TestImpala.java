package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestImpala {

	public static void main(String[] args) throws Exception {
		Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
		Connection con = DriverManager.getConnection("jdbc:hive2://192.168.110.221:10000/default", "", "");
		PreparedStatement sts = con.prepareStatement("select count(*) b,sum(case when CAST(CONCAT('-',ltescrsrp) AS INT )>-110then 1 else 0 end) a from competitormro group by enbid,ltescearfcn,ltescpci ");
		ResultSet rs = sts.executeQuery();
		boolean last = rs.last();
		System.out.println(rs.getRow());
	}
}
