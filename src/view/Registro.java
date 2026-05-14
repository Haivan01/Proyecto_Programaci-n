package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import model.Tecnico;
import model.Usuario;
import model.UsuarioFinal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Formulario dinámico: muestra campos extra según el rol seleccionado.
 * El alta es transaccional (delegada al DAO).
 */
public class Registro extends JDialog {

    private final JTextField txtUser     = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);
    private final JTextField txtEmail    = new JTextField(15);
    private final JTextField txtNombre   = new JTextField(15);
    private final JTextField txtApe      = new JTextField(15);
    private final JTextField txtDni      = new JTextField(15);

    private final JComboBox<String> cmbRol =
            new JComboBox<>(new String[]{"tecnico", "usuario_final"});

    // Campos específicos de técnico
    private final JTextField txtEspecialidad = new JTextField(15);
    private final JTextField txtTelefono     = new JTextField(15);

    // Campos específicos de usuario final
    private final JTextField txtDepartamento = new JTextField(15);
    private final JTextField txtUbicacion    = new JTextField(15);

    // Etiquetas dinámicas
    private final JLabel lblExtra1 = new JLabel();
    private final JLabel lblExtra2 = new JLabel();
    private JPanel panelExtra;
    private JTextField campoExtra1Actual;
    private JTextField campoExtra2Actual;

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public Registro() {
        super((Frame) null, "Registro de nuevo usuario", true);
        construirUI();
        actualizarCamposExtra();
        setSize(450, 520);
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Registro");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        root.add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        int y = 0;
        addRow(form, g, y++, "Usuario:",     txtUser);
        addRow(form, g, y++, "Contraseña:",  txtPass);
        addRow(form, g, y++, "Email:",       txtEmail);
        addRow(form, g, y++, "Nombre:",      txtNombre);
        addRow(form, g, y++, "Apellidos:",   txtApe);
        addRow(form, g, y++, "DNI:",         txtDni);
        addRow(form, g, y++, "Rol:",         cmbRol);

        // Panel dinámico extra
        panelExtra = new JPanel(new GridBagLayout());
        g.gridx = 0; g.gridy = y; g.gridwidth = 2;
        form.add(panelExtra, g);

        cmbRol.addActionListener(e -> actualizarCamposExtra());

        root.add(form, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> dispose());
        JButton btnGuardar = new JButton("Registrar");
        btnGuardar.addActionListener(e -> registrar());
        botones.add(btnCancel);
        botones.add(btnGuardar);
        root.add(botones, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void addRow(JPanel form, GridBagConstraints g, int y, String label, JComponent comp) {
        g.gridx = 0; g.gridy = y; g.gridwidth = 1; g.weightx = 0;
        form.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 1;
        form.add(comp, g);
    }

    /** Cambia los campos extra según el rol elegido. */
    private void actualizarCamposExtra() {
        panelExtra.removeAll();
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        String rol = (String) cmbRol.getSelectedItem();
        if ("tecnico".equals(rol)) {
            lblExtra1.setText("Especialidad:");
            lblExtra2.setText("Teléfono:");
            campoExtra1Actual = txtEspecialidad;
            campoExtra2Actual = txtTelefono;
        } else {
            lblExtra1.setText("Departamento:");
            lblExtra2.setText("Ubicación:");
            campoExtra1Actual = txtDepartamento;
            campoExtra2Actual = txtUbicacion;
        }

        g.gridx = 0; g.gridy = 0; g.weightx = 0; panelExtra.add(lblExtra1, g);
        g.gridx = 1; g.weightx = 1;              panelExtra.add(campoExtra1Actual, g);
        g.gridx = 0; g.gridy = 1; g.weightx = 0; panelExtra.add(lblExtra2, g);
        g.gridx = 1; g.weightx = 1;              panelExtra.add(campoExtra2Actual, g);

        panelExtra.revalidate();
        panelExtra.repaint();
    }

    private void registrar() {
        if (txtUser.getText().trim().isEmpty() || txtPass.getPassword().length == 0
                || txtEmail.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty() || txtApe.getText().trim().isEmpty()
                || txtDni.getText().trim().isEmpty()
                || campoExtra1Actual.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena todos los campos obligatorios.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (usuarioDAO.existeUsername(txtUser.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese username.",
                        "Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario base = new Usuario(
                    0,
                    txtUser.getText().trim(),
                    new String(txtPass.getPassword()),
                    txtEmail.getText().trim(),
                    txtNombre.getText().trim(),
                    txtApe.getText().trim(),
                    txtDni.getText().trim(),
                    Usuario.Rol.valueOf((String) cmbRol.getSelectedItem())
            );

            if (base.getRol() == Usuario.Rol.tecnico) {
                Tecnico t = new Tecnico(base, txtEspecialidad.getText().trim(),
                                              txtTelefono.getText().trim());
                usuarioDAO.registrarTecnico(t);
            } else {
                UsuarioFinal uf = new UsuarioFinal(base, txtDepartamento.getText().trim(),
                                                          txtUbicacion.getText().trim());
                usuarioDAO.registrarUsuarioFinal(uf);
            }

            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar: " + ex.getMessage() + "\n(transacción revertida)",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
