package view;

import dao.*;
import dto.AsignacionDTO;
import dto.IncidenciaDTO;
import dto.UsuarioDTO;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Dashboard principal. Permite gestionar Usuarios, Incidencias y Asignaciones.
 * Panel lateral con navegación + JTable central + formulario adaptativo.
 */
public class Principal extends JFrame {

    private final Usuario usuarioActivo;

    private final UsuarioDAO    usuarioDAO    = new UsuarioDAOImpl();
    private final IncidenciaDAO incidenciaDAO = new IncidenciaDAOImpl();
    private final AsignacionDAO asignacionDAO = new AsignacionDAOImpl();

    private final JTable tabla = new JTable();
    private DefaultTableModel modelo;

    // Estado de navegación
    private enum Modulo { USUARIOS, INCIDENCIAS, ASIGNACIONES }
    private Modulo moduloActivo = Modulo.INCIDENCIAS;

    private final JPanel panelOperaciones = new JPanel(new BorderLayout());

    // Campos del formulario por módulo (creados bajo demanda)
    // Usuario
    private JTextField fUser, fEmail, fNombre, fApe, fDni;
    private JPasswordField fPass;
    private JComboBox<String> fRol;
    private JTextField fExtra1, fExtra2; // especialidad/telefono o depto/ubicacion
    private JLabel lExtra1, lExtra2;
    // Incidencia
    private JTextField fTitulo, fUbicacion;
    private JTextArea  fDescripcion;
    private JComboBox<Incidencia.Prioridad> fPrioridad;
    private JComboBox<Incidencia.Estado>    fEstado;
    private JComboBox<UsuarioFinal>         fReportador;
    // Asignación
    private JComboBox<Tecnico>    fTecnico;
    private JComboBox<Incidencia> fIncidencia;
    private JTextField fFecha;
    private JTextField fObservaciones;
    private JComboBox<Asignacion.EstadoAsignacion> fEstadoAsig;

    public Principal(Usuario u) {
        super("Gestor de Incidencias - IES Francisco Ayala");
        this.usuarioActivo = u;
        construirUI();
        cambiarModulo(Modulo.INCIDENCIAS);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void construirUI() {
        setJMenuBar(crearMenuBar());

        JPanel root = new JPanel(new BorderLayout());

        // -- Cabecera con info del usuario
        JLabel bienvenida = new JLabel("  Sesión: " + usuarioActivo.getUsername()
                + "  |  Rol: " + usuarioActivo.getRol());
        bienvenida.setBorder(new EmptyBorder(6, 10, 6, 10));
        root.add(bienvenida, BorderLayout.NORTH);

        // -- Panel lateral de navegación
        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBorder(new EmptyBorder(10, 10, 10, 10));
        lateral.setPreferredSize(new Dimension(180, 0));

        JButton bUsr = botonModulo("Usuarios",     Modulo.USUARIOS);
        JButton bInc = botonModulo("Incidencias",  Modulo.INCIDENCIAS);
        JButton bAsi = botonModulo("Asignaciones", Modulo.ASIGNACIONES);
        lateral.add(new JLabel("MÓDULOS"));
        lateral.add(Box.createVerticalStrut(8));
        lateral.add(bUsr);
        lateral.add(Box.createVerticalStrut(6));
        lateral.add(bInc);
        lateral.add(Box.createVerticalStrut(6));
        lateral.add(bAsi);
        root.add(lateral, BorderLayout.WEST);

        // -- Centro: tabla + operaciones
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setLeftComponent(new JScrollPane(tabla));
        panelOperaciones.setPreferredSize(new Dimension(360, 0));
        panelOperaciones.setBorder(BorderFactory.createTitledBorder("Operaciones"));
        split.setRightComponent(panelOperaciones);
        split.setResizeWeight(0.65);
        root.add(split, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JButton botonModulo(String texto, Modulo m) {
        JButton b = new JButton(texto);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        b.addActionListener(e -> cambiarModulo(m));
        return b;
    }

    private JMenuBar crearMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu mSesion = new JMenu("Sesión");
        JMenuItem miPass   = new JMenuItem("Cambiar contraseña");
        JMenuItem miSalir  = new JMenuItem("Cerrar sesión");
        miPass.addActionListener(e -> cambiarPassword());
        miSalir.addActionListener(e -> { dispose(); new Login().setVisible(true); });
        mSesion.add(miPass);
        mSesion.addSeparator();
        mSesion.add(miSalir);

        JMenu mPrefs = new JMenu("Preferencias");
        JMenu temas = new JMenu("Tema");
        ButtonGroup bg = new ButtonGroup();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(info.getName());
            item.addActionListener(e -> aplicarTema(info.getClassName()));
            if (UIManager.getLookAndFeel().getName().equals(info.getName())) {
                item.setSelected(true);
            }
            bg.add(item);
            temas.add(item);
        }
        mPrefs.add(temas);

        JMenu mAyuda = new JMenu("Ayuda");
        JMenuItem miAcerca = new JMenuItem("Acerca de…");
        miAcerca.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Gestor de Incidencias\nIES Francisco Ayala\nProyecto Final de Programación",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        mAyuda.add(miAcerca);

        mb.add(mSesion);
        mb.add(mPrefs);
        mb.add(mAyuda);
        return mb;
    }

    private void aplicarTema(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo aplicar el tema.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarPassword() {
        JPasswordField actual = new JPasswordField();
        JPasswordField nueva  = new JPasswordField();
        JPasswordField conf   = new JPasswordField();
        Object[] msg = {"Actual:", actual, "Nueva:", nueva, "Confirmar:", conf};
        int op = JOptionPane.showConfirmDialog(this, msg, "Cambiar contraseña",
                JOptionPane.OK_CANCEL_OPTION);
        if (op != JOptionPane.OK_OPTION) return;
        String n = new String(nueva.getPassword());
        if (!n.equals(new String(conf.getPassword())) || n.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La confirmación no coincide.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            boolean ok = usuarioDAO.cambiarPassword(usuarioActivo.getId(),
                    new String(actual.getPassword()), n);
            if (ok) {
                usuarioActivo.setPassword(n);
                JOptionPane.showMessageDialog(this, "Contraseña actualizada.");
            } else {
                JOptionPane.showMessageDialog(this, "Contraseña actual incorrecta.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    //  Cambio de módulo: reconstruye tabla y formulario lateral
    // ============================================================
    private void cambiarModulo(Modulo m) {
        this.moduloActivo = m;
        ((TitledBorder) panelOperaciones.getBorder()).setTitle("Operaciones — " + m);
        panelOperaciones.removeAll();

        switch (m) {
            case USUARIOS:     mostrarUsuarios();     break;
            case INCIDENCIAS:  mostrarIncidencias();  break;
            case ASIGNACIONES: mostrarAsignaciones(); break;
        }
        panelOperaciones.revalidate();
        panelOperaciones.repaint();
    }

    // ============================================================
    //  MÓDULO USUARIOS
    // ============================================================
    private void mostrarUsuarios() {
        try {
            List<UsuarioDTO> lista = usuarioDAO.listarTodos();
            modelo = new DefaultTableModel(
                    new Object[]{"ID", "Username", "Nombre", "Email", "DNI", "Rol", "Info extra"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (UsuarioDTO u : lista) {
                modelo.addRow(new Object[]{u.getId(), u.getUsername(), u.getNombreCompleto(),
                        u.getEmail(), u.getDni(), u.getRol(), u.getInfoExtra()});
            }
            tabla.setModel(modelo);
        } catch (SQLException ex) {
            mostrarError(ex);
        }
        construirFormularioUsuario();
    }

    private void construirFormularioUsuario() {
        fUser  = new JTextField();
        fPass  = new JPasswordField();
        fEmail = new JTextField();
        fNombre = new JTextField();
        fApe   = new JTextField();
        fDni   = new JTextField();
        fRol   = new JComboBox<>(new String[]{"tecnico", "usuario_final"});
        fExtra1 = new JTextField();
        fExtra2 = new JTextField();
        lExtra1 = new JLabel();
        lExtra2 = new JLabel();

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 3, 3, 3);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        int y = 0;
        addPar(form, g, y++, "Username:", fUser);
        addPar(form, g, y++, "Password:", fPass);
        addPar(form, g, y++, "Email:",    fEmail);
        addPar(form, g, y++, "Nombre:",   fNombre);
        addPar(form, g, y++, "Apellidos:", fApe);
        addPar(form, g, y++, "DNI:",      fDni);
        addPar(form, g, y++, "Rol:",      fRol);
        addPar(form, g, y++, lExtra1, fExtra1);
        addPar(form, g, y++, lExtra2, fExtra2);

        Runnable refrescar = () -> {
            if ("tecnico".equals(fRol.getSelectedItem())) {
                lExtra1.setText("Especialidad:"); lExtra2.setText("Teléfono:");
            } else {
                lExtra1.setText("Departamento:"); lExtra2.setText("Ubicación:");
            }
        };
        refrescar.run();
        fRol.addActionListener(e -> refrescar.run());

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bNuevo    = new JButton("Nuevo");
        JButton bGuardar  = new JButton("Guardar");
        JButton bEliminar = new JButton("Eliminar");
        bNuevo.addActionListener(e -> limpiarFormulario());
        bGuardar.addActionListener(e -> guardarUsuario());
        bEliminar.addActionListener(e -> eliminarUsuario());
        botones.add(bNuevo); botones.add(bGuardar); botones.add(bEliminar);

        panelOperaciones.add(new JScrollPane(form), BorderLayout.CENTER);
        panelOperaciones.add(botones, BorderLayout.SOUTH);
    }

    private void guardarUsuario() {
        try {
            Usuario base = new Usuario(0,
                    fUser.getText().trim(),
                    new String(fPass.getPassword()),
                    fEmail.getText().trim(),
                    fNombre.getText().trim(),
                    fApe.getText().trim(),
                    fDni.getText().trim(),
                    Usuario.Rol.valueOf((String) fRol.getSelectedItem()));
            if ("tecnico".equals(fRol.getSelectedItem())) {
                usuarioDAO.registrarTecnico(new Tecnico(base,
                        fExtra1.getText().trim(), fExtra2.getText().trim()));
            } else {
                usuarioDAO.registrarUsuarioFinal(new UsuarioFinal(base,
                        fExtra1.getText().trim(), fExtra2.getText().trim()));
            }
            JOptionPane.showMessageDialog(this, "Usuario creado.");
            cambiarModulo(Modulo.USUARIOS);
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void eliminarUsuario() {
        int row = tabla.getSelectedRow();
        if (row < 0) { avisarSeleccion(); return; }
        int id = (int) modelo.getValueAt(row, 0);
        if (confirmar("¿Eliminar al usuario " + modelo.getValueAt(row, 1) + "?")) {
            try {
                usuarioDAO.eliminar(id);
                JOptionPane.showMessageDialog(this, "Usuario eliminado.");
                cambiarModulo(Modulo.USUARIOS);
            } catch (SQLException ex) { mostrarError(ex); }
        }
    }

    // ============================================================
    //  MÓDULO INCIDENCIAS
    // ============================================================
    private void mostrarIncidencias() {
        try {
            List<IncidenciaDTO> lista = incidenciaDAO.listarTodos();
            modelo = new DefaultTableModel(
                    new Object[]{"ID", "Título", "Ubicación", "Prioridad", "Estado", "Fecha", "Reportador"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (IncidenciaDTO i : lista) {
                modelo.addRow(new Object[]{i.getId(), i.getTitulo(), i.getUbicacion(),
                        i.getPrioridad(), i.getEstado(), i.getFechaCreacion(), i.getReportador()});
            }
            tabla.setModel(modelo);
        } catch (SQLException ex) { mostrarError(ex); }
        construirFormularioIncidencia();
    }

    private void construirFormularioIncidencia() {
        fTitulo = new JTextField();
        fDescripcion = new JTextArea(3, 15);
        fDescripcion.setLineWrap(true);
        fDescripcion.setWrapStyleWord(true);
        fUbicacion = new JTextField();
        fPrioridad = new JComboBox<>(Incidencia.Prioridad.values());
        fEstado    = new JComboBox<>(Incidencia.Estado.values());
        fReportador = new JComboBox<>();
        try {
            for (UsuarioFinal uf : usuarioDAO.listarUsuariosFinales()) fReportador.addItem(uf);
        } catch (SQLException ex) { mostrarError(ex); }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 3, 3, 3);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        int y = 0;
        addPar(form, g, y++, "Título:",      fTitulo);
        addPar(form, g, y++, "Descripción:", new JScrollPane(fDescripcion));
        addPar(form, g, y++, "Ubicación:",   fUbicacion);
        addPar(form, g, y++, "Prioridad:",   fPrioridad);
        addPar(form, g, y++, "Estado:",      fEstado);
        addPar(form, g, y++, "Reportador:",  fReportador);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bNuevo    = new JButton("Nuevo");
        JButton bGuardar  = new JButton("Guardar");
        JButton bEliminar = new JButton("Eliminar");
        bNuevo.addActionListener(e -> limpiarFormulario());
        bGuardar.addActionListener(e -> guardarIncidencia());
        bEliminar.addActionListener(e -> eliminarIncidencia());
        botones.add(bNuevo); botones.add(bGuardar); botones.add(bEliminar);

        panelOperaciones.add(new JScrollPane(form), BorderLayout.CENTER);
        panelOperaciones.add(botones, BorderLayout.SOUTH);

        // Al hacer clic en la tabla, cargar valores
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (moduloActivo != Modulo.INCIDENCIAS) return;
            int r = tabla.getSelectedRow();
            if (r < 0) return;
            fTitulo.setText((String) modelo.getValueAt(r, 1));
            fUbicacion.setText((String) modelo.getValueAt(r, 2));
            fPrioridad.setSelectedItem(Incidencia.Prioridad.valueOf((String) modelo.getValueAt(r, 3)));
            fEstado.setSelectedItem(Incidencia.Estado.valueOf((String) modelo.getValueAt(r, 4)));
        });
    }

    private void guardarIncidencia() {
        int row = tabla.getSelectedRow();
        UsuarioFinal rep = (UsuarioFinal) fReportador.getSelectedItem();
        if (rep == null) {
            JOptionPane.showMessageDialog(this, "No hay reportadores. Crea primero un usuario final.");
            return;
        }
        try {
            if (row < 0) {
                // INSERT
                Incidencia i = new Incidencia(0,
                        fTitulo.getText().trim(),
                        fDescripcion.getText().trim(),
                        fUbicacion.getText().trim(),
                        (Incidencia.Prioridad) fPrioridad.getSelectedItem(),
                        (Incidencia.Estado)    fEstado.getSelectedItem(),
                        null,
                        rep.getId());
                incidenciaDAO.insertar(i);
                JOptionPane.showMessageDialog(this, "Incidencia creada con id " + i.getId());
            } else {
                // UPDATE
                int id = (int) modelo.getValueAt(row, 0);
                Incidencia i = new Incidencia(id,
                        fTitulo.getText().trim(),
                        fDescripcion.getText().trim(),
                        fUbicacion.getText().trim(),
                        (Incidencia.Prioridad) fPrioridad.getSelectedItem(),
                        (Incidencia.Estado)    fEstado.getSelectedItem(),
                        null,
                        rep.getId());
                incidenciaDAO.actualizar(i);
                JOptionPane.showMessageDialog(this, "Incidencia actualizada.");
            }
            cambiarModulo(Modulo.INCIDENCIAS);
        } catch (SQLException ex) { mostrarError(ex); }
    }

    private void eliminarIncidencia() {
        int row = tabla.getSelectedRow();
        if (row < 0) { avisarSeleccion(); return; }
        int id = (int) modelo.getValueAt(row, 0);
        if (confirmar("¿Eliminar la incidencia #" + id + "?")) {
            try {
                incidenciaDAO.eliminar(id);
                JOptionPane.showMessageDialog(this, "Incidencia eliminada.");
                cambiarModulo(Modulo.INCIDENCIAS);
            } catch (SQLException ex) { mostrarError(ex); }
        }
    }

    // ============================================================
    //  MÓDULO ASIGNACIONES
    // ============================================================
    private void mostrarAsignaciones() {
        try {
            List<AsignacionDTO> lista = asignacionDAO.listarTodos();
            modelo = new DefaultTableModel(
                    new Object[]{"ID", "Técnico", "Incidencia", "Fecha", "Observaciones", "Estado"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (AsignacionDTO a : lista) {
                modelo.addRow(new Object[]{a.getId(), a.getTecnico(), a.getIncidencia(),
                        a.getFechaAsignacion(), a.getObservaciones(), a.getEstado()});
            }
            tabla.setModel(modelo);
        } catch (SQLException ex) { mostrarError(ex); }
        construirFormularioAsignacion();
    }

    private void construirFormularioAsignacion() {
        fTecnico = new JComboBox<>();
        fIncidencia = new JComboBox<>();
        fFecha = new JTextField(LocalDate.now().toString());
        fObservaciones = new JTextField();
        fEstadoAsig = new JComboBox<>(Asignacion.EstadoAsignacion.values());

        try {
            for (Tecnico t : usuarioDAO.listarTecnicos()) fTecnico.addItem(t);
            for (Incidencia i : incidenciaDAO.listarBasico()) fIncidencia.addItem(i);
        } catch (SQLException ex) { mostrarError(ex); }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 3, 3, 3);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        int y = 0;
        addPar(form, g, y++, "Técnico:",       fTecnico);
        addPar(form, g, y++, "Incidencia:",    fIncidencia);
        addPar(form, g, y++, "Fecha (YYYY-MM-DD):", fFecha);
        addPar(form, g, y++, "Observaciones:", fObservaciones);
        addPar(form, g, y++, "Estado:",        fEstadoAsig);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bNuevo    = new JButton("Nuevo");
        JButton bGuardar  = new JButton("Guardar");
        JButton bEliminar = new JButton("Eliminar");
        bNuevo.addActionListener(e -> limpiarFormulario());
        bGuardar.addActionListener(e -> guardarAsignacion());
        bEliminar.addActionListener(e -> eliminarAsignacion());
        botones.add(bNuevo); botones.add(bGuardar); botones.add(bEliminar);

        panelOperaciones.add(new JScrollPane(form), BorderLayout.CENTER);
        panelOperaciones.add(botones, BorderLayout.SOUTH);
    }

    private void guardarAsignacion() {
        Tecnico    t = (Tecnico) fTecnico.getSelectedItem();
        Incidencia i = (Incidencia) fIncidencia.getSelectedItem();
        if (t == null || i == null) {
            JOptionPane.showMessageDialog(this, "Crea primero técnicos e incidencias.");
            return;
        }
        try {
            LocalDate fecha;
            try { fecha = LocalDate.parse(fFecha.getText().trim()); }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fecha no válida. Formato: AAAA-MM-DD");
                return;
            }
            int row = tabla.getSelectedRow();
            if (row < 0) {
                Asignacion a = new Asignacion(0, t.getId(), i.getId(), fecha,
                        fObservaciones.getText().trim(),
                        (Asignacion.EstadoAsignacion) fEstadoAsig.getSelectedItem());
                asignacionDAO.insertar(a);
                JOptionPane.showMessageDialog(this, "Asignación creada.");
            } else {
                int id = (int) modelo.getValueAt(row, 0);
                asignacionDAO.actualizarEstado(id,
                        (Asignacion.EstadoAsignacion) fEstadoAsig.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Estado actualizado.");
            }
            cambiarModulo(Modulo.ASIGNACIONES);
        } catch (SQLException ex) { mostrarError(ex); }
    }

    private void eliminarAsignacion() {
        int row = tabla.getSelectedRow();
        if (row < 0) { avisarSeleccion(); return; }
        int id = (int) modelo.getValueAt(row, 0);
        if (confirmar("¿Eliminar la asignación #" + id + "?")) {
            try {
                asignacionDAO.eliminar(id);
                JOptionPane.showMessageDialog(this, "Asignación eliminada.");
                cambiarModulo(Modulo.ASIGNACIONES);
            } catch (SQLException ex) { mostrarError(ex); }
        }
    }

    // ============================================================
    //  Utilidades
    // ============================================================
    private void addPar(JPanel form, GridBagConstraints g, int y, String label, JComponent c) {
        g.gridx = 0; g.gridy = y; g.weightx = 0;
        form.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 1;
        form.add(c, g);
    }

    private void addPar(JPanel form, GridBagConstraints g, int y, JLabel label, JComponent c) {
        g.gridx = 0; g.gridy = y; g.weightx = 0;
        form.add(label, g);
        g.gridx = 1; g.weightx = 1;
        form.add(c, g);
    }

    private void limpiarFormulario() {
        tabla.clearSelection();
        cambiarModulo(moduloActivo);
    }

    private void avisarSeleccion() {
        JOptionPane.showMessageDialog(this, "Selecciona una fila de la tabla.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(this, pregunta, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
