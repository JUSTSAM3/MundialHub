package co.edu.unbosque.mundialhubbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * Envía el correo de registro con código de verificación en formato HTML.
     * 
     * @param to       Dirección de correo del destinatario
     * @param code     Código de verificación
     * @param username Nombre de usuario destinatario
     * @throws MessagingException Si ocurre un error en el envío del correo
     */
    @Async
    public void sendRegisterEmail(String to, String code, String username) throws MessagingException {
        String subject = "Verificación de correo - Mundial 2026 Hub";
        MimeMessage mimeMsg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("mundialhub2026@gmail.com");

        String html = buildHtmlConfirmBody(username, code);
        helper.setText(html, true);

        // Adjunta logo inline (comentado en código original)
        // ClassPathResource logo = new
        // ClassPathResource("static/images/logo-advancewars.png");
        // helper.addInline("logoCid", logo);

        javaMailSender.send(mimeMsg);
    }

    @Async
    public void sendWelcomeEmail(String to,  String username) throws MessagingException {
        String subject = "Bienvenida - Mundial 2026 Hub";
        MimeMessage mimeMsg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("mundialhub2026@gmail.com");

        String html = buildHtmlConfirmedBody(username);
        helper.setText(html, true);

        // Adjunta logo inline (comentado en código original)
        // ClassPathResource logo = new
        // ClassPathResource("static/images/logo-advancewars.png");
        // helper.addInline("logoCid", logo);

        javaMailSender.send(mimeMsg);
    }
    /**
     * Construye el cuerpo HTML del correo interpolando el nombre de usuario y el
     * código.
     * 
     * @param username Nombre de usuario
     * @param code     Código de verificación
     * @return HTML completo como String
     */
    private String buildHtmlConfirmBody(String username, String code) {
        var template = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { background-color: #f0f2f5; font-family: 'Trebuchet MS', sans-serif; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 15px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); overflow: hidden; border: 1px solid #e1e4e8; }
                    .header { background: #013478; background: linear-gradient(135deg, #013478 0%%, #0056b3 100%%); color: #ffffff; padding: 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 1.8em; text-transform: uppercase; letter-spacing: 3px; }
                    .header p { margin: 5px 0 0; font-size: 0.9em; opacity: 0.8; }
                    .content { padding: 40px; text-align: center; color: #333; }
                    .welcome-text { font-size: 1.2em; color: #013478; font-weight: bold; }
                    .code-box { background: #f8f9fa; border: 2px solid #28a745; border-radius: 12px; padding: 20px; margin: 25px 0; position: relative; }
                    .code { font-size: 2.5em; font-weight: 900; color: #d62828; letter-spacing: 8px; }
                    .instruction { font-size: 0.95em; color: #666; margin-bottom: 20px; }
                    .footer { background: #013478; color: #ffffff; padding: 15px; text-align: center; font-size: 0.8em; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>⚽ MUNDIAL 2026 HUB ⚽</h1>
                        <p>Canadá • Estados Unidos • México</p>
                    </div>
                    <div class="content">
                        <p class="welcome-text">¡Hola, %s!</p>
                        <p class="instruction">
                            Tu acceso al centro de operaciones del Mundial está casi listo...
                        </p>
                        <div class="code-box">
                            <div class="code">%s</div>
                        </div>
                    </div>
                    <div class="footer">
                        <div class="flags">🇨🇦 🇺🇸 🇲🇽</div>
                        <strong>Mundial 2026 Hub</strong><br/>
                        Ingeniería de Software 1 - Universidad El Bosque
                    </div>
                </div>
            </body>
            </html> """;
        return String.format(template, username, code);
    }
    private String buildHtmlConfirmedBody(String username) {
        var template = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { background-color: #f0f2f5; font-family: 'Trebuchet MS', sans-serif; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 15px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); overflow: hidden; border: 1px solid #e1e4e8; }
                    .header { background: #013478; background: linear-gradient(135deg, #013478 0%%, #0056b3 100%%); color: #ffffff; padding: 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 1.8em; text-transform: uppercase; letter-spacing: 3px; }
                    .header p { margin: 5px 0 0; font-size: 0.9em; opacity: 0.8; }
                    .content { padding: 40px; text-align: center; color: #333; }
                    .welcome-text { font-size: 1.5em; color: #013478; font-weight: bold; margin-bottom: 10px; }
                    .status-badge { display: inline-block; background: #d4edda; color: #155724; padding: 10px 20px; border-radius: 50px; font-weight: bold; margin: 20px 0; border: 1px solid #c3e6cb; }
                    .instruction { font-size: 1em; color: #555; line-height: 1.6; margin-bottom: 30px; }
                    .features { text-align: left; background: #f8f9fa; padding: 20px; border-radius: 10px; margin-bottom: 30px; }
                    .features ul { list-style: none; padding: 0; margin: 0; }
                    .features li { margin-bottom: 10px; color: #444; }
                    .features li:before { content: '✅'; margin-right: 10px; }
                    .btn-cta { display: inline-block; padding: 15px 30px; background-color: #28a745; color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: bold; text-transform: uppercase; transition: background 0.3s; }
                    .footer { background: #013478; color: #ffffff; padding: 20px; text-align: center; font-size: 0.8em; }
                    .flags { font-size: 1.4em; margin-bottom: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🏆 MUNDIAL 2026 HUB 🏆</h1>
                        <p>Canadá • Estados Unidos • México</p>
                    </div>
                    <div class="content">
                        <p class="welcome-text">¡Bienvenid@ a la Jugada, %s!</p>
                        
                        <div class="status-badge">✓ CUENTA ACTIVADA EXITOSAMENTE</div>
                        
                        <p class="instruction">
                            Tu registro en <strong>Mundial 2026 Hub</strong> ha sido confirmado. Ya tienes acceso total a la plataforma para vivir la pasión del fútbol desde tu dispositivo.
                        </p>

                        <div class="features">
                            <ul>
                                <li>Gestiona tu agenda de partidos por ciudad.</li>
                                <li>Compite en las pollas con tu comunidad.</li>
                                <li>Completa tu álbum digital de láminas.</li>
                                <li>Recibe notificaciones oficiales en tiempo real.</li>
                            </ul>
                        </div>
                        
                        <!--<a href="#" class="btn-cta">INGRESAR AL HUB</a>-->
                    </div>
                    <div class="footer">
                        <div class="flags">🇨🇦 🇺🇸 🇲🇽</div>
                        <strong>Mundial 2026 Hub</strong><br/>
                        Ingeniería de Software 1 - Universidad El Bosque<br/>
                        <em>"Tu pasión, nuestra tecnología"</em>
                    </div>
                </div>
            </body>
            </html> """;

        return String.format(template, username);
    }

}
