package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import model.GameState.CardState;
import model.GameState.OfferState;
import model.GameState.PlayerState;
import model.GameState.TrophyState;

/**
 * Gestionnaire de sauvegarde et de chargement des parties. Gère la
 * sérialisation/désérialisation des états de jeu et la conversion entre les
 * objets du jeu et leurs représentations sérialisables. Les sauvegardes sont
 * stockées dans le dossier 'saves/' avec l'extension '.jest'.
 */
public class SaveLoadManager {

	private static final String SAVE_DIR = "saves/";
	private static final String SAVE_EXTENSION = ".jest";

	/**
	 * Sauvegarde l'état complet d'une partie sur disque. Crée le dossier de
	 * sauvegarde si nécessaire.
	 * 
	 * @param gameState l'état de jeu à sauvegarder
	 * @param saveName  le nom de la sauvegarde (sans extension)
	 * @return true si la sauvegarde a réussi, false sinon
	 */
	public static boolean saveGame(GameState gameState, String saveName) {
		// Créer le dossier de sauvegarde s'il n'existe pas
		File saveDir = new File(SAVE_DIR);
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}

		String filePath = SAVE_DIR + saveName + SAVE_EXTENSION;

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
			oos.writeObject(gameState);
			System.out.println("Partie sauvegardée avec succès : " + filePath);
			return true;
		} catch (IOException e) {
			System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Charge une partie depuis un fichier de sauvegarde.
	 * 
	 * @param saveName le nom de la sauvegarde (sans extension)
	 * @return l'état de jeu chargé, ou null en cas d'erreur
	 */
	public static GameState loadGame(String saveName) {
		String filePath = SAVE_DIR + saveName + SAVE_EXTENSION;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			GameState gameState = (GameState) ois.readObject();
			System.out.println("Partie chargée avec succès : " + filePath);
			return gameState;
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Erreur lors du chargement : " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Liste toutes les sauvegardes disponibles dans le dossier 'saves/'.
	 * 
	 * @return la liste des noms de sauvegardes (sans extension)
	 */
	public static List<String> listSaves() {
		List<String> saves = new ArrayList<>();
		File saveDir = new File(SAVE_DIR);

		if (saveDir.exists() && saveDir.isDirectory()) {
			File[] files = saveDir.listFiles((dir, name) -> name.endsWith(SAVE_EXTENSION));
			if (files != null) {
				for (File file : files) {
					String name = file.getName().replace(SAVE_EXTENSION, "");
					saves.add(name);
				}
			}
		}

		return saves;
	}

	/**
	 * Supprime une sauvegarde du disque.
	 * 
	 * @param saveName le nom de la sauvegarde à supprimer (sans extension)
	 * @return true si la suppression a réussi, false sinon
	 */
	public static boolean deleteSave(String saveName) {
		String filePath = SAVE_DIR + saveName + SAVE_EXTENSION;
		File file = new File(filePath);

		if (file.exists()) {
			boolean deleted = file.delete();
			if (deleted) {
				System.out.println("Sauvegarde supprimée : " + saveName);
			}
			return deleted;
		}
		return false;
	}

	/**
	 * Convertit une carte en état de carte sérialisable.
	 * 
	 * @param card la carte à convertir
	 * @return l'état de carte correspondant, ou null si card est null
	 */
	public static CardState toCardState(Card card) {
		if (card == null)
			return null;
		return new CardState(card.getFaceValue(), card.getColor().name(), card.isVisible());
	}

	/**
	 * Convertit un état de carte en carte du jeu.
	 * 
	 * @param cs l'état de carte à convertir
	 * @return la carte correspondante, ou null si cs est null
	 */
	public static Card toCard(CardState cs) {
		if (cs == null)
			return null;
		Card card = new Card(cs.getValue(), Color.valueOf(cs.getColor()));
		card.setVisible(cs.isVisible());
		return card;
	}

	// Convertit un Player en PlayerState
	public static PlayerState toPlayerState(Player player) {
		PlayerState ps = new PlayerState();
		ps.setName(player.getName());
		ps.setHasPlayed(player.hasPlayed());

		// Déterminer le type de stratégie
		if (player.getStrategy() instanceof RealPlayer) {
			ps.setStrategyType("REAL");
		} else if (player.getStrategy() instanceof VirtualPlayerRandom) {
			ps.setStrategyType("RANDOM");
		} else if (player.getStrategy() instanceof VirtualPlayerCheater) {
			ps.setStrategyType("CHEATER");
		}

		// Convertir la main
		for (Card card : player.getHand()) {
			ps.getHand().add(toCardState(card));
		}

		// Convertir le jest
		for (Card card : player.getJest()) {
			ps.getJest().add(toCardState(card));
		}

		return ps;
	}

	// Convertit un PlayerState en Player
	public static Player toPlayer(PlayerState ps) {
		// Créer la stratégie appropriée
		Strategy strategy;
		String strategyType = ps.getStrategyType();

		// Gérer null et les anciennes/nouvelles valeurs pour compatibilité
		if (strategyType == null || strategyType.equals("REAL") || strategyType.equals("RealPlayer")) {
			strategy = new RealPlayer();
		} else if (strategyType.equals("RANDOM") || strategyType.equals("VirtualPlayerRandom")) {
			strategy = new VirtualPlayerRandom();
		} else if (strategyType.equals("CHEATER") || strategyType.equals("VirtualPlayerCheater")) {
			strategy = new VirtualPlayerCheater();
		} else {
			// Par défaut, utiliser RealPlayer
			strategy = new RealPlayer();
		}

		Player player = new Player(ps.getName(), strategy);

		// Restaurer hasPlayed
		if (ps.isHasPlayed()) {
			player.SetHasPlayed();
		}

		// Restaurer la main
		for (CardState cs : ps.getHand()) {
			player.toHand(toCard(cs));
		}

		// Restaurer le jest
		for (CardState cs : ps.getJest()) {
			player.toJest(toCard(cs));
		}

		return player;
	}

	// Convertit une Offer en OfferState
	public static OfferState toOfferState(Offer offer, List<Player> players) {
		OfferState os = new OfferState();

		// Trouver l'index du propriétaire
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) == offer.getOwner()) {
				os.setOwnerIndex(i);
				break;
			}
		}

		os.setVisibleCard(toCardState(offer.getVisibleCard()));
		os.setHiddenCard(toCardState(offer.getHiddenCard()));

		return os;
	}

	// Convertit un OfferState en Offer
	public static Offer toOffer(OfferState os, List<Player> players) {
		Player owner = players.get(os.getOwnerIndex());
		Card visible = toCard(os.getVisibleCard());
		Card hidden = toCard(os.getHiddenCard());

		return new Offer(visible, hidden, owner);
	}

	// Convertit un Trophy en TrophyState
	public static TrophyState toTrophyState(Trophy trophy, List<Player> players) {
		TrophyState ts = new TrophyState();
		ts.setType(trophy.getType().name());
		ts.setSourceCard(toCardState(trophy.getSourceCard()));

		// Trouver l'index du propriétaire (si attribué)
		if (trophy.getOwner() != null) {
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i) == trophy.getOwner()) {
					ts.setOwnerIndex(i);
					break;
				}
			}
		}

		return ts;
	}

	// Convertit un TrophyState en Trophy
	public static Trophy toTrophy(TrophyState ts, List<Player> players) {
		TrophyType type = TrophyType.valueOf(ts.getType());
		Card sourceCard = toCard(ts.getSourceCard());
		Trophy trophy = new Trophy(type, sourceCard);

		// Restaurer le propriétaire si attribué
		if (ts.getOwnerIndex() != null) {
			trophy.setOwner(players.get(ts.getOwnerIndex()));
		}

		return trophy;
	}
}