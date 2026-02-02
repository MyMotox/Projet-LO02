package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente l'état complet d'une partie de Jest. Contient toutes les
 * informations nécessaires pour sauvegarder et restaurer une partie :
 * configuration, joueurs, deck, stack, offres, trophées. Implémente
 * Serializable pour permettre la sauvegarde sur disque.
 */
public class GameState implements Serializable {
	private static final long serialVersionUID = 1L;

	// Configuration de la partie
	private int nbPlayers;
	private int mode;
	private boolean extensionActive;
	// Index du joueur courant (pour UI)
	private int currentPlayerIndex = -1;
	private int currentRound = 0;

	// État des joueurs
	private List<PlayerState> playerStates;

	// État du deck et stack
	private List<CardState> deckCards;
	private List<CardState> stackCards;

	// Cartes trophées
	private CardState trophyCard1;
	private CardState trophyCard2;

	// Offres en cours
	private List<OfferState> currentOffers;

	// Trophées attribués
	private List<TrophyState> trophies;

	/**
	 * Construit un état de jeu vide avec toutes les listes initialisées.
	 */
	public GameState() {
		this.playerStates = new ArrayList<>();
		this.deckCards = new ArrayList<>();
		this.stackCards = new ArrayList<>();
		this.currentOffers = new ArrayList<>();
		this.trophies = new ArrayList<>();
	}

	/**
	 * Retourne le nombre de joueurs de la partie.
	 * 
	 * @return le nombre de joueurs
	 */
	public int getNbPlayers() {
		return nbPlayers;
	}

	/**
	 * Définit le nombre de joueurs de la partie.
	 * 
	 * @param nbPlayers le nombre de joueurs
	 */
	public void setNbPlayers(int nbPlayers) {
		this.nbPlayers = nbPlayers;
	}

	/**
	 * Retourne le mode de jeu (1=Console, 2=GUI).
	 * 
	 * @return le mode de jeu
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Définit le mode de jeu.
	 * 
	 * @param mode le mode de jeu
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * Indique si l'extension est active pour cette partie.
	 * 
	 * @return true si l'extension est active, false sinon
	 */
	public boolean isExtensionActive() {
		return extensionActive;
	}

	public void setExtensionActive(boolean extensionActive) {
		this.extensionActive = extensionActive;
	}

	public List<PlayerState> getPlayerStates() {
		return playerStates;
	}

	public void setPlayerStates(List<PlayerState> playerStates) {
		this.playerStates = playerStates;
	}

	public List<CardState> getDeckCards() {
		return deckCards;
	}

	public void setDeckCards(List<CardState> deckCards) {
		this.deckCards = deckCards;
	}

	public List<CardState> getStackCards() {
		return stackCards;
	}

	public void setStackCards(List<CardState> stackCards) {
		this.stackCards = stackCards;
	}

	public CardState getTrophyCard1() {
		return trophyCard1;
	}

	public void setTrophyCard1(CardState trophyCard1) {
		this.trophyCard1 = trophyCard1;
	}

	public CardState getTrophyCard2() {
		return trophyCard2;
	}

	public void setTrophyCard2(CardState trophyCard2) {
		this.trophyCard2 = trophyCard2;
	}

	public List<OfferState> getCurrentOffers() {
		return currentOffers;
	}

	public void setCurrentOffers(List<OfferState> currentOffers) {
		this.currentOffers = currentOffers;
	}

	public List<TrophyState> getTrophies() {
		return trophies;
	}

	public void setTrophies(List<TrophyState> trophies) {
		this.trophies = trophies;
	}

	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}

	public void setCurrentPlayerIndex(int currentPlayerIndex) {
		this.currentPlayerIndex = currentPlayerIndex;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	/**
	 * Représente l'état d'un joueur pour la sérialisation. Contient le nom, le type
	 * de stratégie, la main, le Jest et l'état de jeu.
	 */
	public static class PlayerState implements Serializable {
		private static final long serialVersionUID = 1L;

		private String name;
		private String strategyType;
		private List<CardState> hand;
		private List<CardState> jest;
		private boolean hasPlayed;

		public PlayerState() {
			this.hand = new ArrayList<>();
			this.jest = new ArrayList<>();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStrategyType() {
			return strategyType;
		}

		public void setStrategyType(String strategyType) {
			this.strategyType = strategyType;
		}

		public List<CardState> getHand() {
			return hand;
		}

		public void setHand(List<CardState> hand) {
			this.hand = hand;
		}

		public List<CardState> getJest() {
			return jest;
		}

		public void setJest(List<CardState> jest) {
			this.jest = jest;
		}

		public boolean isHasPlayed() {
			return hasPlayed;
		}

		public void setHasPlayed(boolean hasPlayed) {
			this.hasPlayed = hasPlayed;
		}
	}

	public static class CardState implements Serializable {
		private static final long serialVersionUID = 1L;

		private int value;
		private String color; // Nom de l'enum Color
		private boolean isVisible;

		public CardState() {
		}

		public CardState(int value, String color, boolean isVisible) {
			this.value = value;
			this.color = color;
			this.isVisible = isVisible;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public boolean isVisible() {
			return isVisible;
		}

		public void setVisible(boolean isVisible) {
			this.isVisible = isVisible;
		}
	}

	public static class OfferState implements Serializable {
		private static final long serialVersionUID = 1L;

		private int ownerIndex; // Index du joueur dans la liste
		private CardState visibleCard;
		private CardState hiddenCard;

		public OfferState() {
		}

		public int getOwnerIndex() {
			return ownerIndex;
		}

		public void setOwnerIndex(int ownerIndex) {
			this.ownerIndex = ownerIndex;
		}

		public CardState getVisibleCard() {
			return visibleCard;
		}

		public void setVisibleCard(CardState visibleCard) {
			this.visibleCard = visibleCard;
		}

		public CardState getHiddenCard() {
			return hiddenCard;
		}

		public void setHiddenCard(CardState hiddenCard) {
			this.hiddenCard = hiddenCard;
		}
	}

	public static class TrophyState implements Serializable {
		private static final long serialVersionUID = 1L;

		private String type; // Nom de l'enum TrophyType
		private CardState sourceCard;
		private Integer ownerIndex; // null si pas encore attribué

		public TrophyState() {
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public CardState getSourceCard() {
			return sourceCard;
		}

		public void setSourceCard(CardState sourceCard) {
			this.sourceCard = sourceCard;
		}

		public Integer getOwnerIndex() {
			return ownerIndex;
		}

		public void setOwnerIndex(Integer ownerIndex) {
			this.ownerIndex = ownerIndex;
		}
	}
}