package view.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import controller.GameController;
import model.Player;

/**
 * Fenêtre d'affichage des scores en fin de partie.
 */
public class ScoreFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private Player winner;
	private List<Player> players;
	private int[] scores;
	private GameController controller;

	/**
	 * Construit la fenêtre d'affichage des scores.
	 * 
	 * @param winner     le joueur gagnant
	 * @param players    la liste de tous les joueurs
	 * @param scores     les scores de chaque joueur
	 * @param controller le contrôleur de jeu
	 */
	public ScoreFrame(Player winner, List<Player> players, int[] scores, GameController controller) {
		this.winner = winner;
		this.players = players;
		this.scores = scores;
		this.controller = controller;

		initializeUI();
	}

	/**
	 * Initialise l'interface de la fenêtre de scores. Affiche le gagnant, tous les
	 * scores et les boutons d'action.
	 */
	private void initializeUI() {
		setTitle("JEST - Fin de partie");
		setSize(600, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		mainPanel.setBackground(new Color(240, 240, 250));

		// Titre
		JLabel titleLabel = new JLabel("🏆 FIN DE LA PARTIE 🏆");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setForeground(new Color(70, 70, 150));
		mainPanel.add(titleLabel);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// Gagnant
		JPanel winnerPanel = new JPanel();
		winnerPanel.setBackground(new Color(255, 215, 0));
		winnerPanel.setBorder(BorderFactory.createLineBorder(new Color(184, 134, 11), 3));
		winnerPanel.setMaximumSize(new Dimension(500, 80));

		JLabel winnerLabel = new JLabel("👑 GAGNANT : " + winner.getName() + " 👑");
		winnerLabel.setFont(new Font("Arial", Font.BOLD, 24));
		winnerPanel.add(winnerLabel);

		mainPanel.add(winnerPanel);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// Scores
		JLabel scoresTitle = new JLabel("📈 SCORES");
		scoresTitle.setFont(new Font("Arial", Font.BOLD, 24));
		scoresTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(scoresTitle);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		JPanel scoresPanel = new JPanel();
		scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));
		scoresPanel.setBackground(Color.WHITE);
		scoresPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

		for (int i = 0; i < players.size(); i++) {
			JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
			scoreRow.setBackground(i % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));

			JLabel playerLabel = new JLabel((i + 1) + ". " + players.get(i).getName());
			playerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
			playerLabel.setPreferredSize(new Dimension(250, 30));
			scoreRow.add(playerLabel);

			JLabel scoreLabel = new JLabel(scores[i] + " points");
			scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
			scoreLabel.setForeground(new Color(0, 100, 0));
			scoreRow.add(scoreLabel);

			scoresPanel.add(scoreRow);
		}

		mainPanel.add(scoresPanel);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// Boutons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
		buttonPanel.setBackground(new Color(240, 240, 250));

		JButton btnSave = new JButton("💾 Sauvegarder");
		btnSave.setFont(new Font("Arial", Font.BOLD, 16));
		btnSave.setBackground(new Color(100, 150, 200));
		btnSave.setForeground(Color.BLACK);
		btnSave.setFocusPainted(false);
		btnSave.setBorderPainted(false);
		btnSave.setPreferredSize(new Dimension(180, 40));
		btnSave.addActionListener(e -> {
			String saveName = JOptionPane.showInputDialog(this, "Nom de la sauvegarde :");
			if (saveName != null && !saveName.trim().isEmpty()) {
				controller.saveGame(saveName.trim());
				JOptionPane.showMessageDialog(this, "Partie sauvegardée !", "Sauvegarde",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		buttonPanel.add(btnSave);

		JButton btnNewGame = new JButton("🎮 Nouvelle Partie");
		btnNewGame.setFont(new Font("Arial", Font.BOLD, 16));
		btnNewGame.setBackground(new Color(100, 200, 100));
		btnNewGame.setForeground(Color.BLACK);
		btnNewGame.setFocusPainted(false);
		btnNewGame.setBorderPainted(false);
		btnNewGame.setPreferredSize(new Dimension(180, 40));
		btnNewGame.addActionListener(e -> {
			this.dispose();
			MainMenuFrame menu = new MainMenuFrame(new GameController());
			menu.setVisible(true);
		});
		buttonPanel.add(btnNewGame);

		JButton btnQuit = new JButton("❌ Quitter");
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
}
