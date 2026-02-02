# 💻 LO02 A25 | JEST GAME 🃏

Jeu de cartes JEST avec interface graphique (GUI) et console, développé en Java avec architecture MVC.

## 👤 Membres
- **ALLABERT Mathéo**
- **CHARLET-SOMLETTE Jules**

## 💻 Langage
<p align="left">
  <a href="https://www.java.com" target="_blank" rel="noreferrer">
    <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="40" height="40"/>
  </a>
</p>

## 🏗️ Architecture du projet

Le projet suit le pattern **MVC** avec 3 packages principaux :

```
project_loo2_jest/
├── src/
│   ├── model/          # Logique métier, données, stratégies, trophées
│   ├── view/           # Interfaces utilisateur (Console + GUI)
│   └── controller/     # Coordination entre Model et View
├── docs/               # Documentation Javadoc (français)
├── resources/          # Images et ressources graphiques
└── saves/              # Sauvegardes des parties (.jest)
```

### 📦 Packages

- **model** : Contient toute la logique du jeu (cartes, joueurs, stratégies, trophées, score, sauvegarde)
- **view** : Deux vues utilisables simultanément (ConsoleView + GameView GUI)
- **controller** : GameController orchestre le jeu et notifie les vues (pattern Observer)

## 🚀 Lancer le jeu

### ✅ Nouvelle version (Recommandée)
Interface moderne avec **Console + GUI simultanées** :

```bash
cd project_loo2_jest
javac -d bin -sourcepath src src/**/*.java
java -cp bin controller.JestGame
```

**Fonctionnalités** :
- 🖥️ Interface graphique avec fond de tapis
- 💬 Console synchronisée en parallèle
- 🎮 Multi-joueurs humains et IA
- 💾 Sauvegarde/Chargement de parties
- 🏆 Affichage des trophées et scores

### 🕰️ Ancienne version (Console uniquement)
Version originale en mode console pur :

```bash
cd project_loo2_jest
javac -d bin -sourcepath src src/**/*.java
java -cp bin controller.ControllerPartie
```

⚠️ *Note : Cette version est obsolète mais reste fonctionnelle.*

## 🕹️ Modes de jeu

- **♠️ JEST Classique** : Règles standard de JEST
- **👑 JEST Bouffon** : Le joueur avec la moins bonne main devient bouffon et commence le round
- **👁️ JEST Clair** : Le stack est visible avant la distribution des cartes

## 🎴 Extension Bonus/Malus/Gold

Cartes spéciales ajoutant de la stratégie :

- **❌ Malus** → -3 points
- **✅ Bonus** → +3 points  
- **🏆 Gold** → +5 points

*Activable au début de chaque partie*

## 🤖 Joueurs Virtuels (IA)

- **🦑 Bender (Random)** : Robot imprévisible qui joue aléatoirement
- **🚨 HAL-9000 (Cheater)** : IA omnisciente qui triche et connaît toutes les cartes

## ♻️ Système de sauvegarde

- 💾 **Sauvegarde** : À tout moment via le bouton "Sauvegarder"
- 📂 **Chargement** : Au démarrage, choisir "Charger une partie"
- 📁 **Format** : Fichiers `.jest` dans le dossier `saves/`
- 🔄 **Compatible** : Sauvegarde l'état complet (joueurs, cartes, offres, trophées)

## 📚 Documentation

Documentation Javadoc complète en français disponible :

```bash
open docs/index.html
```

Ou consultez [JAVADOC.md](project_loo2_jest/JAVADOC.md) pour plus d'informations.

## 🔧 Compilation et tests

```bash
# Compilation
cd project_loo2_jest
javac -d bin -sourcepath src src/**/*.java

# Génération de la Javadoc
javadoc -d docs -sourcepath src -subpackages model:view:controller -encoding UTF-8

# Exécution
java -cp bin controller.JestGame
```

## 📝 Notes techniques

- **Java Version** : Compatible Java 8+
- **GUI Framework** : Java Swing
- **Sérialisation** : Java Serialization pour la persistence
- **Patterns** : MVC, Observer, Strategy, Visitor

---

🎮 **Bon jeu !**



