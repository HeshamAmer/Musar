package musar;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;



     
     /**
     *
     */
    public class connecting {
     public static String [][] ResultArray ;
      public static int NumOfCols ;
      public static int NumOfRows ;
        public static Statement stmt = null;
        public static Connection con;
       
      public connecting() throws ClassNotFoundException, SQLException
      {
          //dppBZScb5smhHFFu
            Class.forName("com.mysql.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://jbossews-musar.rhcloud.com/jbossews?" +"user=admin881Rhs7&password=9JDlLn1rh4l6";
            con = DriverManager.getConnection(connectionUrl);
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            //ResultSet rs = null;
      }
      public static String[][] Conn(String Query,boolean Insert) {
     
      try
       {
            
            ResultSet rs = null;
            if(Insert)
            {
                ResultArray = new String[1][1];
                ResultArray[0][0]="0" ;/// Success
                stmt = con.createStatement();
                int rowsEffected = stmt.executeUpdate(Query);
                System.out.println(rowsEffected + " rows effected");
                ResultArray[0][0]="1" ;/// Success
            }
            else
            {
                stmt = con.createStatement();
                rs = stmt.executeQuery(Query);
                ResultSetMetaData rsmd = rs.getMetaData();
                NumOfCols = rsmd.getColumnCount();
                rs.last();
                NumOfRows = rs.getRow();
                rs.beforeFirst();
                ResultArray = new String [NumOfRows][NumOfCols];

          //   System.out.println(NumOfRows);
          //   System.out.println(NumOfCols);

             while (rs.next())
                    {
                        for(int j = 1 ; j <= NumOfCols ; j++)
                        {
                        
                        ResultArray [rs.getRow()-1][j-1] = rs.getString(j);
                        }
                    }
            }

       }

       catch (SQLException e)
       {
            System.out.println("SQL Exception: "+ e.toString());
            System.out.println("SQL CAUSED THE PROBLEM :" + Query);
       }

     
      return ResultArray;
  }
  
     }
    
     