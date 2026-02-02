package view.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import controller.GameController;
import view.ConsoleView;

/**
 * Menu principal du jeu JEST (interface graphique). Permet de configurer et
 * démarrer une nouvelle partie ou charger une sauvegarde.
 */
public class MainMenuFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private GameController controller;
	private JSpinner spinnerHumans;
	private JSpinner spinnerBots;
	private JComboBox<String> comboMode;
	private JCheckBox checkExtension;
	private JPanel panelPlayerNames;
	private List<JTextField> playerNameFields;

	/**
	 * Construit le menu principal.
	 * 
	 * @param controller le contrôleur de jeu à utiliser
	 */
	public MainMenuFrame(GameController controller) {
		this.controller = controller;
		this.playerNameFields = new ArrayList<>();

		initializeUI();
	}

	/**
	 * Initialise l'interface du menu principal. Crée tous les composants de
	 * configuration et les boutons.
	 */
	private void initializeUI() {
		setTitle("JEST - Menu Principal");
		setSize(600, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);

		// Panel principal
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		mainPanel.setBackground(new Color(240, 240, 250));

		// Titre
		JLabel titleLabel = new JLabel("JEST");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setForeground(new Color(70, 70, 150));
		mainPanel.add(titleLabel);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		JLabel subtitleLabel = new JLabel("The Card Game");
		subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 20));
		subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		subtitleLabel.setForeground(new Color(100, 100, 150));
		mainPanel.add(subtitleLabel);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

		// Configuration des joueurs
		JPanel configPanel = new JPanel();
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
		configPanel.setBackground(Color.WHITE);
		configPanel
				.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(70, 70, 150), 2),
						"Configuration de la partie", 0, 0, new Font("Arial", Font.BOLD, 14), new Color(70, 70, 150)));

		// Nombre de joueurs humains
		JPanel humansPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		humansPanel.setBackground(Color.WHITE);
		humansPanel.add(new JLabel("Joueurs humains (0-4) :"));
		spinnerHumans = new JSpinner(new SpinnerNumberModel(1, 0, 4, 1));
		spinnerHumans.addChangeListener(e -> updatePlayerNamesPanel());
		humansPanel.add(spinnerHumans);
		configPanel.add(humansPanel);

		// Nombre de bots
		JPanel botsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		botsPanel.setBackground(Color.WHITE);
		botsPanel.add(new JLabel("Robots (0-4) :"));
		spinnerBots = new JSpinner(new SpinnerNumberModel(1, 0, 4, 1));
		botsPanel.add(spinnerBots);
		configPanel.add(botsPanel);

		// Mode de jeu
		JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		modePanel.setBackground(Color.WHITE);
		modePanel.add(new JLabel("Mode de jeu :"));
		comboMode = new JComboBox<>(new String[] { "JEST Classique", "JEST Bouffon (moins bonne main commence)",
				"JEST Clair (stack visible)" });
		modePanel.add(comboMode);
		configPanel.add(modePanel);

		// Extension
		JPanel extPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		extPanel.setBackground(Color.WHITE);
		checkExtension = new JCheckBox("Activer extension Bonus/Malus/Gold");
		extPanel.add(checkExtension);
		configPanel.add(extPanel);

		mainPanel.add(configPanel);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// Panel pour les noms des joueurs
		panelPlayerNames = new JPanel();
		panelPlayerNames.setLayout(new BoxLayout(panelPlayerNames, BoxLayout.Y_AXIS));
		panelPlayerNames.setBackground(Color.WHITE);
		panelPlayerNames
				.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(70, 70, 150), 2),
						"Noms des joueurs", 0, 0, new Font("Arial", Font.BOLD, 14), new Color(70, 70, 150)));

		updatePlayerNamesPanel();
		mainPanel.add(panelPlayerNames);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// Boutons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
		buttonPanel.setBackground(new Color(240, 240, 250));

		JButton btnNewGame = new JButton("Nouvelle Partie");
		btnNewGame.setFont(new Font("Arial", Font.BOLD, 16));
		btnNewGame.setBackground(new Color(100, 200, 100));
		btnNewGame.setForeground(Color.BLACK);
		btnNewGame.setFocusPainted(false);
		btnNewGame.setBorderPainted(false);
		btnNewGame.setPreferredSize(new Dimension(180, 40));
		btnNewGame.addActionListener(e -> startNewGame());
		buttonPanel.add(btnNewGame);

		JButton btnLoad = new JButton("Charger");
		btnLoad.setFont(new Font("Arial", Font.BOLD, 16));
		btnLoad.setBackground(new Color(100, 150, 200));
		btnLoad.setForeground(Color.BLACK);
		btnLoad.setFocusPainted(false);
		btnLoad.setBorderPainted(false);
		btnLoad.setPreferredSize(new Dimension(180, 40));
		btnLoad.addActionListener(e -> loadGame());
		buttonPanel.add(btnLoad);

		JButton btnQuit = new JButton("Quitter");
		btnQuit.setFont(new Font("Arial", Font.BOLD, 16));
		btnQuit.setBackground(new Color(200, 100, 100));
		btnQuit.setForeground(Color.BLACK);
		btnQuit.setFocusPainted(false);
		btnQuit.setBorderPainted(false);
		btnQuit.setPreferredSize(new Dimension(180, 40));
		btnQuit.addActionListener(e -> System.exit(0));
		buttonPanel.add(btnQuit);

		mainPanel.add(buttonPanel);

		add(new JScrollPane(mainPanel));
	}

	private void updatePlayerNamesPanel() {
		panelPlayerNames.removeAll();
		playerNameFields.clear();

		int nbHumans = (Integer) spinnerHumans.getValue();

		for (int i = 0; i < nbHumans; i++) {
			JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			namePanel.setBackground(Color.WHITE);
			namePanel.add(new JLabel("Joueur " + (i + 1) + " :"));
			JTextField nameField = new JTextField(15);
			nameField.setText("Joueur " + (i + 1));
			namePanel.add(nameField);
			panelPlayerNames.add(namePanel);
			playerNameFields.add(nameField);
		}

		panelPlayerNames.revalidate();
		panelPlayerNames.repaint();
	}

	private void startNewGame() {
		int nbHumans = (Integer) spinnerHumans.getValue();
		int nbBots = (Integer) spinnerBots.getValue();

		// Validation
		if (nbHumans + nbBots < 2) {
			JOptionPane.showMessageDialog(this, "Il faut au moins 2 joueurs pour commencer une partie !", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (nbHumans + nbBots > 4) {
			JOptionPane.showMessageDialog(this, "Maximum 4 joueurs autorisés !", "Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Récupérer les noms des joueurs
		List<String> playerNames = new ArrayList<>();
		for (JTextField field : playerNameFields) {
			String name = field.getText().trim();
			if (name.isEmpty()) {
				name = "Joueur " + (playerNames.size() + 1);
			}
			playerNames.add(name);
		}

		// Configuration des bots
		List<Boolean> botTypes = new ArrayList<>();
		if (nbBots > 0) {
			String[] botOptions = { "Robot Aléatoire", "Robot Tricheur" };
			for (int i = 0; i < nbBots; i++) {
				int choice = JOptionPane.showOptionDialog(this, "Choisissez le type pour le robot " + (i + 1),
						"Configuration Robot", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						botOptions, botOptions[0]);
				botTypes.add(choice == 1); // true = tricheur, false = aléatoire
			}
		}

		int mode = comboMode.getSelectedIndex();
		boolean extension = checkExtension.isSelected();

		// Créer la console view pour les interactions
		ConsoleView consoleView = new ConsoleView(controller);

		// Créer la vue graphique
		GameView gameView = new GameView(controller);

		// Configurer la stratégie pour utiliser les deux vues
		consoleView.getPlayerStrategy().setSecondaryView(gameView);
		gameView.setPlayerStrategy(consoleView.getPlayerStrategy());

		controller.setHumanStrategy(consoleView.getPlayerStrategy());
		controller.startNewGame(nbHumans, nbBots, playerNames, botTypes, mode, extension);

		// Cacher le menu et afficher la fenêtre de jeu
		this.setVisible(false);
		gameView.setVisible(true);

		// Lancer le jeu dans un thread séparé
		new Thread(() -> {
			controller.runGameLoop();
		}).start();
	}

	private void loadGame() {
		List<String> saves = controller.getAvailableSaves();

		if (saves.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Aucune sauvegarde trouvée.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String[] saveArray = saves.toArray(new String[0]);
		String selected = (String) JOptionPane.showInputDialog(this, "Choisissez une sauvegarde :",
				"Charger une partie", JOptionPane.PLAIN_MESSAGE, null, saveArray, saveArray[0]);

		if (selected != null) {
			// Créer la console view pour les interactions
			ConsoleView consoleView = new ConsoleView(controller);

			// Créer la vue graphique
			GameView gameView = new GameView(controller);

			// Configurer la stratégie pour utiliser les deux vues
			consoleView.getPlayerStrategy().setSecondaryView(gameView);
			gameView.setPlayerStrategy(consoleView.getPlayerStrategy());

			controller.setHumanStrategy(consoleView.getPlayerStrategy());

			// Cacher le menu et afficher la fenêtre de jeu AVANT de charger
			this.setVisible(false);
			gameView.setVisible(true);

			// Charger la partie APRÈS avoir configuré et affiché les vues
			controller.loadGame(selected);

			// Lancer le jeu dans un thread séparé
			new Thread(() -> {
				controller.runGameLoop();
			}).start();
		}
	}
}
