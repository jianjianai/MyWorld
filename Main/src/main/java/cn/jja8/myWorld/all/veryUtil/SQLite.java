package cn.jja8.myWorld.all.veryUtil;

import java.sql.*;
/**
 * @author jianjianai
 * */
public class SQLite {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection git连接(String 团队数据库URL){
        try {
            return DriverManager.getConnection(团队数据库URL);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new Error("数据库连接错误！");
        }
    }
    /**
     * 测试用
     * */
    public static PreparedStatementAndUp get预编译语句(String sql,String 团队数据库URL){
        PreparedStatementAndUp preparedStatementAndUp = new PreparedStatementAndUp();
        preparedStatementAndUp.connection = git连接(团队数据库URL);
        try {
            preparedStatementAndUp.preparedStatement = preparedStatementAndUp.connection.prepareStatement(sql);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new Error("sql语句错误！");
        }
        return preparedStatementAndUp;
    }


    public static class PreparedStatementAndUp{
        /**
         * 执行本sql语句
         * */
        public boolean execute(){
            try {
                return preparedStatement.execute();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("sql语句执行错误！");
            }
        }
        /**
         * 执行本sql查询语句，并获得查询结果
         * */
        public ResultSetAndUp executeQuery(){
            try {
                ResultSetAndUp resultSetAndUp =new ResultSetAndUp();
                resultSetAndUp.preparedStatementAndUp = this;
                resultSetAndUp.ResultSet = preparedStatement.executeQuery();
                return resultSetAndUp;
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("sql语句执行错误！");
            }
        }
        void setDouble(int parameterIndex, double x){
            try {
                preparedStatement.setDouble(parameterIndex,x);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("sql参数错误");
            }
        }
        void setFloat(int parameterIndex, float x){
            try {
                preparedStatement.setFloat(parameterIndex,x);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("sql参数错误");
            }
        }
        public void setInt(int parameterIndex, int x){
            try {
                preparedStatement.setInt(parameterIndex,x);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("sql参数错误");
            }
        }
        void setLong(int parameterIndex, long x){
            try {
                preparedStatement.setLong(parameterIndex,x);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("sql参数错误");
            }
        }
        public void setString(int parameterIndex, String x){
            try {
                preparedStatement.setString(parameterIndex,x);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("sql参数错误");
            }
        }
        public void close(){
            try {
                preparedStatement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        public Connection connection;
        public PreparedStatement preparedStatement;

        public int executeUpdate() {
            try {
                return preparedStatement.executeUpdate();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("sql语句执行错误！");
            }
        }
    }
    public static class ResultSetAndUp{
        public PreparedStatementAndUp preparedStatementAndUp;
        public ResultSet ResultSet;
        public void close(){
            try {
                ResultSet.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        /**
         从此 ResultSet对象和基础数据库中删除当前行。
         * */
        public void deleteRow(){
            try {
                ResultSet.deleteRow();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("无法删除数据");
            }
        }
        /**
         * 将光标从当前位置向前移动一行。 ResultSet光标最初位于第一行之前; 第一次调用方法next使第一行成为当前行; 第二个调用使第二行成为当前行，依此类推。
         * 当对next方法的调用返回false ，光标位于最后一行之后。 任何需要当前行的ResultSet方法的调用都将导致抛出SQLException 。 如果结果集类型为TYPE_FORWARD_ONLY ，它是指定的JDBC驱动程序实现是否会返回供应商false或抛出SQLException上的后续调用next 。
         * */
        public boolean next(){
            try {
                return ResultSet.next();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                return false;
            }
        }

        public int getInt(int i) {
            try {
                return ResultSet.getInt(i);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("获取数据错误");
            }
        }

        public String getString(int i) {
            try {
                return ResultSet.getString(i);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new Error("获取数据错误");
            }
        }
    }

}

