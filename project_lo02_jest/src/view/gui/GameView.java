package view.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import controller.GameController;
import model.Card;
import model.Color; // Notre enum de couleurs de cartes
import model.InteractivePlayerStrategy;
import model.Offer;
import model.Player;
import view.GameViewInterface;

/**
 * Vue principale du jeu JEST (interface graphique). Affiche l'état du jeu, les
 * cartes, et gère les interactions utilisateur.
 */
public class GameView extends JFrame implements GameViewInterface {

	private static final long serialVersionUID = 1L;

	private GameController controller;
	private InteractivePlayerStrategy playerStrategy;

	// Composants UI
	private JPanel mainPanel;
	private BufferedImage carpetImage;
	private JPanel trophyPanel;
	private JPanel playersPanel;
	private JPanel actionPanel;
	private JTextArea logArea;
	private JButton btnSave;

	// État de l'interface
	private BlockingQueue<Integer> inputQueue;
	private Map<String, ImageIcon> cardImages;

	// État du jeu
	private List<Player> currentPlayers;
	private Player currentPlayer;
	private boolean isChoosingPhase; // true pendant la phase de choix de cible/carte

	/**
	 * Construit la vue graphique du jeu. Initialise l'interface, charge les images
	 * de cartes et s'enregistre comme observateur.
	 * 
	 * @param controller le contrôleur de jeu à observer
	 */
	public GameView(GameController controller) {
		this.controller = controller;
		this.inputQueue = new LinkedBlockingQueue<>();
		this.playerStrategy = new InteractivePlayerStrategy(this);
		this.cardImages = new HashMap<>();
		this.controller.addObserver(this);

		loadCardImages();
		initializeUI();
	}

	/**
	 * Retourne la stratégie de joueur interactif de cette vue.
	 * 
	 * @return la stratégie de joueur interactif
	 */
	public InteractivePlayerStrategy getPlayerStrategy() {
		return playerStrategy;
	}

	/**
	 * Définit la stratégie de joueur interactif à utiliser.
	 * 
	 * @param strategy la nouvelle stratégie
	 */
	public void setPlayerStrategy(InteractivePlayerStrategy strategy) {
		this.playerStrategy = strategy;
	}

	/**
	 * Charge les images de cartes et l'image de fond du tapis.
	 */
	private void loadCardImages() {
		// Charger l'image de fond du tapis
		try {
			File carpetFile = new File("resources/img/carpet.png");
			if (carpetFile.exists()) {
				carpetImage = javax.imageio.ImageIO.read(carpetFile);
			}
		} catch (Exception e) {
			System.err.println("Erreur lors du chargement de carpet.png : " + e.getMessage());
		}
	}

	/**
	 * Initialise l'interface graphique avec tous les composants. Crée les panels
	 * pour les trophées, joueurs, actions et logs.
	 */
	private void initializeUI() {
		setTitle("JEST - Partie en cours");
		setSize(1200, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// Créer un panel avec image de fond
		mainPanel = new JPanel(new BorderLayout(10, 10)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (carpetImage != null) {
					// Dessiner l'image en mosaïque pour couvrir tout le panel
					int imgWidth = carpetImage.getWidth();
					int imgHeight = carpetImage.getHeight();
					for (int x = 0; x < getWidth(); x += imgWidth) {
						for (int y = 0; y < getHeight(); y += imgHeight) {
							g.drawImage(carpetImage, x, y, null);
						}
					}
				}
			}
		};
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setOpaque(false);

		// Panel supérieur : Trophées et informations
		trophyPanel = new JPanel();
		trophyPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		trophyPanel.setOpaque(false);
		trophyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(java.awt.Color.YELLOW, 2),
				"Cartes Trophées", 0, 0, new Font("Arial", Font.BOLD, 16), java.awt.Color.YELLOW));
		mainPanel.add(trophyPanel, BorderLayout.NORTH);

		// Panel central : Joueurs et leurs cartes
		playersPanel = new JPanel();
		playersPanel.setLayout(new GridLayout(2, 2, 10, 10));
		playersPanel.setOpaque(false);
		mainPanel.add(playersPanel, BorderLayout.CENTER);

		// Panel inférieur : Actions et log
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(false);

		actionPanel = new JPanel();
		actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		actionPanel.setOpaque(false);
		bottomPanel.add(actionPanel, BorderLayout.NORTH);

		// Zone de log
		logArea = new JTextArea(8, 40);
		logArea.setEditable(false);
		logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		logArea.setBackground(new java.awt.Color(240, 240, 240));
		logArea.setForeground(java.awt.Color.BLACK);
		JScrollPane scrollPane = new JScrollPane(logArea);
		scrollPane.setPreferredSize(new Dimension(600, 150));
		bottomPanel.add(scrollPane, BorderLayout.CENTER);

		// Bouton de sauvegarde
		JPanel savePanel = new JPanel();
		savePanel.setOpaque(false);
		btnSave = new JButton("💾 Sauvegarder");
		btnSave.setFont(new Font("Arial", Font.BOLD, 14));
		btnSave.addActionListener(e -> saveGame());
		savePanel.add(btnSave);
		bottomPanel.add(savePanel, BorderLayout.SOUTH);

		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		add(mainPanel);
	}

	private void saveGame() {
		String saveName = JOptionPane.showInputDialog(this, "Nom de la sauvegarde :");
		if (saveName != null && !saveName.trim().isEmpty()) {
			controller.saveGame(saveName.trim());
		}
	}

	private void log(String message) {
		SwingUtilities.invokeLater(() -> {
			logArea.append(message + "\n");
			logArea.setCaretPosition(logArea.getDocument().getLength());
		});
	}

	// === CRÉATION DE CARTES GRAPHIQUES ===

	private JPanel createCardPanel(Card card, boolean isHidden) {
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(new BorderLayout());
		cardPanel.setPreferredSize(new Dimension(100, 140));
		cardPanel.setBackground(java.awt.Color.WHITE);
		cardPanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK, 2));

		if (isHidden) {
			// Carte cachée
			JLabel backLabel = new JLabel("?", SwingConstants.CENTER);
			backLabel.setFont(new Font("Arial", Font.BOLD, 72));
			backLabel.setForeground(new java.awt.Color(139, 69, 19));
			cardPanel.add(backLabel, BorderLayout.CENTER);

			JLabel hiddenLabel = new JLabel("CACHÉE", SwingConstants.CENTER);
			hiddenLabel.setFont(new Font("Arial", Font.BOLD, 10));
			cardPanel.add(hiddenLabel, BorderLayout.SOUTH);
		} else if (card != null) {
			// Carte visible
			ImageIcon icon = getCardImage(card);
			if (icon != null) {
				JLabel imageLabel = new JLabel(icon);
				cardPanel.add(imageLabel, BorderLayout.CENTER);
			} else {
				// Placeholder textuel
				JPanel infoPanel = new JPanel();
				infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
				infoPanel.setBackground(getColorForCard(card.getColor()));

				JLabel colorLabel = new JLabel(getColorSymbol(card.getColor()), SwingConstants.CENTER);
				colorLabel.setFont(new Font("Arial", Font.BOLD, 48));
				colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				infoPanel.add(colorLabel);

				JLabel valueLabel = new JLabel(String.valueOf(card.getFaceValue()), SwingConstants.CENTER);
				valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
				valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				infoPanel.add(valueLabel);

				cardPanel.add(infoPanel, BorderLayout.CENTER);
			}
		} else {
			// Carte vide
			JLabel emptyLabel = new JLabel("VIDE", SwingConstants.CENTER);
			emptyLabel.setFont(new Font("Arial", Font.BOLD, 16));
			emptyLabel.setForeground(java.awt.Color.GRAY);
			cardPanel.add(emptyLabel, BorderLayout.CENTER);
		}

		return cardPanel;
	}

	private ImageIcon getCardImage(Card card) {
		// Tentative de chargement de l'image depuis resources/img
		String filename = "resources/img/" + card.getColor().toString().toLowerCase() + "_" + card.getFaceValue()
				+ ".png";
		File imageFile = new File(filename);

		if (imageFile.exists()) {
			try {
				ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
				Image img = icon.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH);
				return new ImageIcon(img);
			} catch (Exception e) {
				// Utiliser le placeholder
			}
		}

		// Sinon, utiliser placeholder générique
		String placeholderPath = "resources/img/placeholder_card.png";
		File placeholderFile = new File(placeholderPath);
		if (placeholderFile.exists()) {
			try {
				ImageIcon icon = new ImageIcon(placeholderFile.getAbsolutePath());
				Image img = icon.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH);
				return new ImageIcon(img);
			} catch (Exception e) {
				// Ignorer
			}
		}

		return null;
	}

	private java.awt.Color getColorForCard(Color color) {
		switch (color) {
		case HEART:
			return new java.awt.Color(255, 200, 200);
		case SPADE:
			return new java.awt.Color(200, 200, 200);
		case CLUB:
			return new java.awt.Color(180, 255, 180);
		case DIAMOND:
			return new java.awt.Color(200, 220, 255);
		case JOKER:
			return new java.awt.Color(255, 255, 150);
		case BONUS:
			return new java.awt.Color(150, 255, 150);
		case MALUS:
			return new java.awt.Color(255, 150, 150);
		case GOLD:
			return new java.awt.Color(255, 215, 0);
		default:
			return java.awt.Color.WHITE;
		}
	}

	private String getColorSymbol(Color color) {
		switch (color) {
		case HEART:
			return "♥";
		case SPADE:
			return "♠";
		case CLUB:
			return "♣";
		case DIAMOND:
			return "♦";
		case JOKER:
			return "🃏";
		case BONUS:
			return "⭐";
		case MALUS:
			return "⚠";
		case GOLD:
			return "💰";
		default:
			return "?";
		}
	}

	// === MÉTHODES D'INTERACTION ===

	@Override
	public int askForOffer(Player player) {
		inputQueue.clear();

		// Vérifier si le joueur est humain
		boolean isHuman = player.getStrategy() instanceof InteractivePlayerStrategy;

		SwingUtilities.invokeLater(() -> {
			actionPanel.removeAll();

			if (isHuman) {
				JLabel label = new JLabel(player.getName() + " - Choisissez la carte à CACHER :");
				label.setFont(new Font("Arial", Font.BOLD, 16));
				label.setForeground(java.awt.Color.YELLOW);
				actionPanel.add(label);

				// Afficher les boutons UNIQUEMENT pour les joueurs humains
				if (player.getHand().size() >= 2) {
					JButton btn1 = new JButton("Cacher Carte 1");
					btn1.addActionListener(e -> {
						inputQueue.offer(1);
						// Envoyer aussi à la stratégie
						if (playerStrategy != null) {
							playerStrategy.provideResponse(1);
						}
					});
					actionPanel.add(btn1);

					JButton btn2 = new JButton("Cacher Carte 2");
					btn2.addActionListener(e -> {
						inputQueue.offer(2);
						// Envoyer aussi à la stratégie
						if (playerStrategy != null) {
							playerStrategy.provideResponse(2);
						}
					});
					actionPanel.add(btn2);
				}
			} else {
				// Pour les bots, afficher un message informatif
				JLabel label = new JLabel(player.getName() + " (Bot) joue automatiquement...");
				label.setFont(new Font("Arial", Font.ITALIC, 14));
				label.setForeground(java.awt.Color.LIGHT_GRAY);
				actionPanel.add(label);
			}

			actionPanel.revalidate();
			actionPanel.repaint();
		});

		try {
			return inputQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 1;
		}
	}

	@Override
	public int askForTarget(Player player, List<Offer> availableOffers) {
		inputQueue.clear();

		// Vérifier si le joueur est humain
		boolean isHuman = player.getStrategy() instanceof InteractivePlayerStrategy;

		SwingUtilities.invokeLater(() -> {
			actionPanel.removeAll();

			if (isHuman) {
				JLabel label = new JLabel(player.getName() + " - Choisissez une cible :");
				label.setFont(new Font("Arial", Font.BOLD, 16));
				label.setForeground(java.awt.Color.YELLOW);
				actionPanel.add(label);

				// Afficher les boutons UNIQUEMENT pour les joueurs humains
				for (int i = 0; i < availableOffers.size(); i++) {
					final int index = i + 1;
					Offer offer = availableOffers.get(i);
					JButton btn = new JButton(offer.getOwner().getName());
					btn.addActionListener(e -> {
						inputQueue.offer(index);
						// Envoyer aussi à la stratégie
						if (playerStrategy != null) {
							playerStrategy.provideResponse(index);
						}
					});
					actionPanel.add(btn);
				}
			} else {
				// Pour les bots, afficher un message informatif
				JLabel label = new JLabel(player.getName() + " (Bot) choisit automatiquement...");
				label.setFont(new Font("Arial", Font.ITALIC, 14));
				label.setForeground(java.awt.Color.LIGHT_GRAY);
				actionPanel.add(label);
			}

			actionPanel.revalidate();
			actionPanel.repaint();
		});

		try {
			return inputQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 1;
		}
	}

	@Override
	public int askForCard(Player player, Offer targetOffer) {
		inputQueue.clear();

		// Vérifier si le joueur est humain
		boolean isHuman = player.getStrategy() instanceof InteractivePlayerStrategy;

		SwingUtilities.invokeLater(() -> {
			actionPanel.removeAll();

			if (isHuman) {
				JLabel label = new JLabel(player.getName() + " - Choisissez une carte :");
				label.setFont(new Font("Arial", Font.BOLD, 16));
				label.setForeground(java.awt.Color.YELLOW);
				actionPanel.add(label);

				// Afficher les boutons UNIQUEMENT pour les joueurs humains
				if (targetOffer.getVisibleCard() != null) {
					JButton btnVisible = new JButton("Carte Visible: " + targetOffer.getVisibleCard());
					btnVisible.addActionListener(e -> {
						inputQueue.offer(1);
						// Envoyer aussi à la stratégie
						if (playerStrategy != null) {
							playerStrategy.provideResponse(1);
						}
					});
					actionPanel.add(btnVisible);
				}

				if (targetOffer.getHiddenCard() != null) {
					JButton btnHidden = new JButton("Carte Cachée: ???");
					btnHidden.addActionListener(e -> {
						inputQueue.offer(2);
						// Envoyer aussi à la stratégie
						if (playerStrategy != null) {
							playerStrategy.provideResponse(2);
						}
					});
					actionPanel.add(btnHidden);
				}
			} else {
				// Pour les bots, afficher un message informatif
				JLabel label = new JLabel(player.getName() + " (Bot) choisit automatiquement...");
				label.setFont(new Font("Arial", Font.ITALIC, 14));
				label.setForeground(java.awt.Color.LIGHT_GRAY);
				actionPanel.add(label);
			}

			actionPanel.revalidate();
			actionPanel.repaint();
		});

		try {
			return inputQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 1;
		}
	}

	@Override
	public void displayMessage(String message) {
		log(message);
	}

	@Override
	public void start() {
		setVisible(true);
	}

	@Override
	public void close() {
		dispose();
	}

	// === OBSERVATEUR ===

	@Override
	public void onGameStarted(int nbPlayers, int mode, boolean extensionActive) {
		log("✅ Partie démarrée !");
		log("Joueurs : " + nbPlayers);
		String modeStr = mode == 0 ? "Classique" : (mode == 1 ? "Bouffon" : "Clair");
		log("Mode : " + modeStr);
		log("Extension : " + (extensionActive ? "Activée" : "Désactivée"));
	}

	@Override
	public void onRoundStarted(int roundNumber) {
		log("\n====== 🎮 ROUND " + roundNumber + " 🎮 ======");
	}

	@Override
	public void onCardsDistributed(List<Player> players) {
		this.currentPlayers = players;
		log("✅ Cartes distribuées à tous les joueurs.");
		updatePlayersDisplay();
	}

	@Override
	public void onPlayerTurnToOffer(Player player) {
		this.currentPlayer = player;
		this.isChoosingPhase = false; // Phase d'offre, on affiche la main
		log("\n====== Tour de " + player.getName() + " ======");
		updatePlayersDisplay();
	}

	@Override
	public void onOfferMade(Player player, Offer offer) {
		log(player.getName() + " a proposé " + offer.getVisibleCard() + " / CACHÉE");

		// Nettoyer le panneau d'actions après l'offre
		SwingUtilities.invokeLater(() -> {
			actionPanel.removeAll();
			actionPanel.revalidate();
			actionPanel.repaint();
		});

		updatePlayersDisplay();
	}

	@Override
	public void onPlayerTurnToChoose(Player player, List<Offer> availableOffers) {
		this.currentPlayer = player;
		this.isChoosingPhase = true; // Phase de choix, on affiche seulement les offres
		log("\n====== " + player.getName() + " doit choisir une carte ======");

		if (availableOffers.isEmpty()) {
			log("Aucune offre disponible.");
		} else {
			log("Offres disponibles :");
			for (int i = 0; i < availableOffers.size(); i++) {
				Offer o = availableOffers.get(i);
				log("(" + (i + 1) + ") " + o.getOwner().getName() + " - " + o.getVisibleCard() + " / CACHÉE");
			}
		}

		updatePlayersDisplay();
	}

	@Override
	public void onCardChosen(Player chooser, Player target, Card card) {
		if (chooser == target) {
			log(chooser.getName() + " prend de sa main " + card);
		} else {
			log(chooser.getName() + " prend " + card + " depuis " + target.getName());
		}

		// Nettoyer le panneau d'actions après le choix de carte
		SwingUtilities.invokeLater(() -> {
			actionPanel.removeAll();
			actionPanel.revalidate();
			actionPanel.repaint();
		});

		updatePlayersDisplay();
	}

	@Override
	public void onRoundEnded(List<Player> players) {
		log("\n====== Fin du round ======");
		log("Jest des joueurs :");
		for (Player p : players) {
			log(p.getName() + " : " + p.getJest());
		}
		updatePlayersDisplay();

		// La sauvegarde peut être faite à tout moment via le bouton dédié
	}

	@Override
	public void onGameEnded(Player winner, List<Player> players, int[] scores) {
		log("\n====== 🏆 FIN DE LA PARTIE 🏆 ======");
		log("\n====== 📈 SCORES 📈 ======");
		for (int i = 0; i < players.size(); i++) {
			log(players.get(i).getName() + " : " + scores[i] + " points");
		}
		log("\n====== 👑 GAGNANT 👑 ======");
		log("Félicitations à " + winner.getName() + " 👑 qui remporte le jeu !");

		SwingUtilities.invokeLater(() -> {
			// Afficher popup de sauvegarde non-bloquante
			int choice = JOptionPane.showConfirmDialog(this, "Voulez-vous sauvegarder cette partie ?", "Sauvegarde",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (choice == JOptionPane.YES_OPTION) {
				String saveName = JOptionPane.showInputDialog(this, "Entrez le nom de la sauvegarde :",
						"Nom de sauvegarde", JOptionPane.PLAIN_MESSAGE);
				if (saveName != null && !saveName.trim().isEmpty()) {
					controller.saveGame(saveName.trim());
				}
			}

			// Afficher l'écran des scores
			ScoreFrame scoreFrame = new ScoreFrame(winner, players, scores, controller);
			scoreFrame.setVisible(true);
			this.dispose();
		});
	}

	@Override
	public void onTrophyCardsDisplayed(Card trophy1, Card trophy2) {
		SwingUtilities.invokeLater(() -> {
			trophyPanel.removeAll();

			JLabel trophyLabel = new JLabel("🏆 TROPHÉES 🏆");
			trophyLabel.setFont(new Font("Arial", Font.BOLD, 18));
			trophyLabel.setForeground(java.awt.Color.YELLOW);
			trophyPanel.add(trophyLabel);

			trophyPanel.add(createCardPanel(trophy1, false));

			if (trophy2 != null) {
				trophyPanel.add(createCardPanel(trophy2, false));
			}

			trophyPanel.revalidate();
			trophyPanel.repaint();
		});
	}

	@Override
	public void onStackDisplayed(List<Card> stack) {
		log("👁️ JEST Clair - Stack visible :");
		for (Card c : stack) {
			log("  " + c.getColor() + " " + c.getFaceValue());
		}
	}

	@Override
	public void onGameSaved(String saveName) {
		log("✅ Partie sauvegardée : " + saveName);
		JOptionPane.showMessageDialog(this, "Partie sauvegardée avec succès !", "Sauvegarde",
				JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void onGameLoaded(String saveName) {
		log("✅ Partie chargée : " + saveName);

		// Récupérer l'état du jeu et mettre à jour l'affichage
		this.currentPlayers = controller.getPlayers();

		// Afficher les informations de la partie
		log("Joueurs : " + controller.getNbPlayers());
		String modeStr = controller.getMode() == 0 ? "Classique" : (controller.getMode() == 1 ? "Bouffon" : "Clair");
		log("Mode : " + modeStr);
		log("Extension : " + (controller.isExtensionActive() ? "Activée" : "Désactivée"));
		log("Round actuel : " + controller.getCurrentRound());

		// Afficher les cartes trophées
		Card trophy1 = controller.getTrophyCard1();
		Card trophy2 = controller.getTrophyCard2();
		onTrophyCardsDisplayed(trophy1, trophy2);

		// Mettre à jour l'affichage des joueurs et cartes
		// Important : faire ça APRÈS les trophées pour que tout soit visible
		updatePlayersDisplay();

		log("\n⏯️ Reprise de la partie au round " + controller.getCurrentRound() + "...");
	}

	@Override
	public void onError(String errorMessage) {
		log("❌ ERREUR : " + errorMessage);
		JOptionPane.showMessageDialog(this, errorMessage, "Erreur", JOptionPane.ERROR_MESSAGE);
	}

	// === MISE À JOUR DE L'AFFICHAGE ===

	private void updatePlayersDisplay() {
		SwingUtilities.invokeLater(() -> {
			playersPanel.removeAll();

			if (currentPlayers != null) {
				for (Player player : currentPlayers) {
					JPanel playerPanel = createPlayerPanel(player);
					playersPanel.add(playerPanel);
				}
			}

			playersPanel.revalidate();
			playersPanel.repaint();
		});
	}

	private JPanel createPlayerPanel(Player player) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(new java.awt.Color(50, 100, 50));

		boolean isCurrentPlayer = (player == currentPlayer);
		java.awt.Color borderColor = isCurrentPlayer ? java.awt.Color.YELLOW : java.awt.Color.WHITE;
		int borderWidth = isCurrentPlayer ? 3 : 1;

		panel.setBorder(BorderFactory.createLineBorder(borderColor, borderWidth));

		// Nom du joueur
		JLabel nameLabel = new JLabel(player.getName(), SwingConstants.CENTER);
		nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
		nameLabel.setForeground(isCurrentPlayer ? java.awt.Color.YELLOW : java.awt.Color.WHITE);
		panel.add(nameLabel, BorderLayout.NORTH);

		// Cartes
		JPanel cardsPanel = new JPanel();
		cardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		cardsPanel.setBackground(new java.awt.Color(50, 100, 50));

		// Main du joueur - VISIBLE UNIQUEMENT pendant la phase d'offre ET pour le
		// joueur actif ET si c'est un humain
		boolean isHuman = player.getStrategy() instanceof InteractivePlayerStrategy;
		if (!isChoosingPhase && !player.getHand().isEmpty() && isCurrentPlayer && isHuman) {
			JLabel handLabel = new JLabel("Main: ");
			handLabel.setForeground(java.awt.Color.WHITE);
			cardsPanel.add(handLabel);

			for (Card card : player.getHand()) {
				cardsPanel.add(createCardPanel(card, false));
			}
		}

		// Offre du joueur - Afficher uniquement la carte VISIBLE
		if (currentPlayers != null) {
			for (model.Offer offer : controller.getOffers()) {
				if (offer.getOwner() == player) {
					JLabel offerLabel = new JLabel(" | Offre: ");
					offerLabel.setForeground(java.awt.Color.YELLOW);
					cardsPanel.add(offerLabel);

					// Carte visible
					if (offer.getVisibleCard() != null) {
						cardsPanel.add(createCardPanel(offer.getVisibleCard(), false));
					}

					// Carte cachée (toujours masquée sauf pour le joueur lui-même)
					if (offer.getHiddenCard() != null) {
						if (isCurrentPlayer) {
							// Le joueur voit sa propre carte cachée
							cardsPanel.add(createCardPanel(offer.getHiddenCard(), false));
						} else {
							// Les autres voient juste qu'il y a une carte cachée
							cardsPanel.add(createCardPanel(null, true));
						}
					}
					break;
				}
			}
		}

		// Jest du joueur (cartes collectées) - Toujours visible (nombre uniquement)
		if (!player.getJest().isEmpty()) {
			JLabel jestLabel = new JLabel(" | Jest: ");
			jestLabel.setForeground(java.awt.Color.WHITE);
			cardsPanel.add(jestLabel);

			JLabel jestCount = new JLabel("(" + player.getJest().size() + " cartes)");
			jestCount.setForeground(java.awt.Color.CYAN);
			cardsPanel.add(jestCount);
		}

		panel.add(cardsPanel, BorderLayout.CENTER);

		return panel;
	}
}
