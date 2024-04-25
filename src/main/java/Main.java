import java.io.FileInputStream;
import java.sql.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        conexionMySQL();
    }

    static void conexionMySQL(){
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:4306/ejemplo";
            String user = "root";
            String password = "";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a la base de datos");
            System.out.println("Prueba con sabrina");
            if (conn != null) {
                operationDB(conn);
            } else {
                System.out.println("No existe conexión");
            }

        } catch (SQLException e) {
            System.out.println("Error en la conexión a la base de datos: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }

    static void operationDB(Connection conn) throws SQLException {
        String insertQuery = "INSERT INTO usuarios (nombre, edad) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(insertQuery);
        pstmt.setString(1, "Juan");
        pstmt.setInt(2, 30);
        pstmt.executeUpdate();
        System.out.println("Datos insertados correctamente");

        String selectQuery = "SELECT * FROM usuarios";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(selectQuery);
        StringBuilder finalMessage = new StringBuilder();
        while (rs.next()) {
            String message = "ID: " + rs.getInt("id") + ", Nombre: " + rs.getString("nombre") + ", Edad: " + rs.getInt("edad");
            System.out.println(message);
            finalMessage.append("<p>").append(message).append("</p>");
        }
        sendEmail(finalMessage);
    }

    static void sendEmail(StringBuilder finalMessage){
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");


        try {
            FileInputStream in = new FileInputStream("src/main/resources/secrets.properties");
            properties.load(in);
            in.close();

            final String myAccountEmail = properties.getProperty("MY_ACCOUNT_EMAIL");
            final String password = properties.getProperty("MY_ACCOUNT_PASSWORD");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(myAccountEmail, password);
                }
            });
            Message message = prepareMessage(session, myAccountEmail, "anaplopez07@gmail.com", finalMessage);
            if(message != null){
                Transport.send(message);
            }

            System.out.println("Mensaje enviado exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Message prepareMessage(Session session, String myAccountEmail, String recipient, StringBuilder finalMessage) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Asunto del correo");
            String htmlCode = "<h1>Hola</h1> <br/> <h2>Bienvenido!</h2>";
            htmlCode += "<p> Estos son los usuarios existentes en la base de datos: </p>";
            htmlCode += finalMessage;
            message.setContent(htmlCode, "text/html");
            return message;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void Login(){
        //hola probando
    }
}