package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class Login extends JFrame {

    private final JTextField txtUser = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public Login() {
        super("Gestor de Incidencias - IES Francisco Ayala | Login");
        construirUI();
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void construirUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Iniciar sesión");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        panel.add(titulo, g);

        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 1; panel.add(new JLabel("Usuario:"), g);
        g.gridx = 1; panel.add(txtUser, g);

        g.gridx = 0; g.gridy = 2; panel.add(new JLabel("Contraseña:"), g);
        g.gridx = 1; panel.add(txtPass, g);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(e -> intentarLogin());
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        panel.add(btnEntrar, g);

        JLabel enlaceRegistro = new JLabel("<html><a href=''>¿No tienes cuenta? Regístrate aquí</a></html>");
        enlaceRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        enlaceRegistro.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                new Registro().setVisible(true);
            }
        });
        g.gridy = 4;
        panel.add(enlaceRegistro, g);

        // Enter dispara el login
        getRootPane().setDefaultButton(btnEntrar);

        setContentPane(panel);
    }

    private void intentarLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena usuario y contraseña.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Usuario u = usuarioDAO.validar(user, pass);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Abrir dashboard y cerrar login
            new Principal(u).setVisible(true);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error de conexión: " + ex.getMessage(),
                    "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }
}
