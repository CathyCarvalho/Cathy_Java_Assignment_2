import warrior.*;
import weapon.*;
import armour.*;
import weather.*;
import utility.*;

import java.util.Scanner;
import java.util.Random;

public class Warsim {

    // Objects
    public static Scanner input = new Scanner(System.in);
    public static Random randNum = new Random();
    public static Ink ink = new Ink();
    public static Weather weather;

    // Player Objects
    public static Warrior player; // player
    public static Weapon pWeapon; // player weapon
    public static Armour pArmour; // player armour

    // Enemy Objects
    public static Warrior enemy; // enemy
    public static Weapon eWeapon; // enemy weapon
    public static Armour eArmour; // enemy armour

    // Variables
    public static boolean gameOver = false;
    public static boolean playerTurn = true;
    public static int choice = 0;
    public static int attackType = 0;
    public static int damage = 0;
    public static String who = "Player";
    public static String winner = "";

    public static void main(String[] args) {
        ink.welcomeMessage();

        String playerName = ink.getPlayerName(); // Prompt player name

        // set a random weather for the battle
        int weatherType = randNum.nextInt(4) + 1;
        createWeather(weatherType);

        //===============>>
        // Player Setup

        who = playerName; // Assign the player's name

        // Warrior
        ink.printWarriorMenu();
        int playerChoice = input.nextInt(); // 1, 2, or 3
        createWarrior(who, playerName, playerChoice); 


        // Weapon Setup
        ink.printWeaponMenu();
        int weaponChoice = input.nextInt(); // 1, 2 or 3
        createWeapon("Player", weaponChoice);

        // Armour Setup
        ink.printArmourMenu();
        int armourChoice = input.nextInt(); // 1, 2 or 3
        createArmour("Player", armourChoice);

        who = "Enemy"; // swap the who with the what

        //===============>>
        // Enemy Setup
        // Warrior
        choice = randNum.nextInt(3) + 1;
        createWarrior(who, playerName, choice);

        // Weapon Setup
        choice = randNum.nextInt(3) + 1;
        createWeapon(who, choice);

        // Armour Setup
        choice = randNum.nextInt(3) + 1;
        createArmour(who, choice);

        ink.printStats(playerName, player, enemy, pArmour, 
        eArmour, pWeapon, eWeapon);

        // main game loop
        while (!gameOver) { // while the game is NOT over
            if (playerTurn) {
                ink.printAttackMenu();
                attackType = input.nextInt();
                damage = pWeapon.strike(weather.getSeverity(), attackType, player.getStrength(), player.getDexterity());
                damage = eArmour.reduceDamage(damage);
                enemy.takeDamage(damage);
                if (!enemy.isAlive()) {
                    winner = "Player";
                    gameOver = true;
                }
                // Critical hit chance
                int playerCriticalChance = 0;
                switch (pWeapon.getType()) {
                    case "Dagger":
                         playerCriticalChance = 25;
                         break;
                    case "Sword":
                         playerCriticalChance = 10;
                         break;
                    case "Axe":
                         playerCriticalChance = 5;
                         break;
                    default:
                         break;
                }
                if (randNum.nextInt(100) + 1 <= playerCriticalChance) {
                    System.out.println("Player scored a critical hit!");
                    damage *= 2;
                }
            } else {
                // Enemy's Turn
                System.out.println("Enemy's Turn!");
                ink.printAttackMenu();
                attackType = randNum.nextInt(2) + 1;
                // Calculate base damage inflicted by the enemy's weapon
                damage = eWeapon.strike(weather.getSeverity(), attackType, enemy.getStrength(), enemy.getDexterity());
                // Calculate critical chance based on the enemy's weapon
                int enemyCriticalChance = 0;
                switch (eWeapon.getType()) {
                    case "Dagger":
                        enemyCriticalChance = 25;
                        break;
                    case "Sword":
                        enemyCriticalChance = 10;
                        break;
                    case "Axe":
                        enemyCriticalChance = 5;
                        break;
                    default:
                        break;
                }
                // Check for critical hit
                if (randNum.nextInt(100) + 1 <= enemyCriticalChance) {
                    // Critical hit doubles the damage
                    damage *= 2;
                    System.out.println("Enemy scored a critical hit!");
                }
                // Apply player's armor evasion chance
                int totalPlayerEvasion = pArmour.getEvasion();
                switch (pArmour.getType()) {
                    case "Leather":
                        totalPlayerEvasion += 50;
                        break;
                    case "Chainmail":
                        totalPlayerEvasion += 25;
                        break;
                    case "Platemail":
                        totalPlayerEvasion += 10;
                        break;
                    default:
                        break;
                }
                // Check if the attack was evaded by the player's armor
                if (randNum.nextInt(100) + 1 <= totalPlayerEvasion) {
                    // Player's armor successfully evaded the attack
                    System.out.println("Player's armor evaded the attack!");
                    damage = 0; // No damage inflicted
                }
                // Apply damage to the player
                player.takeDamage(damage);
                // Check if player is defeated
                if (!player.isAlive()) {
                    winner = "Enemy";
                    gameOver = true;
                }
            }
            ink.printStats(playerName, player, enemy, pArmour, 
            eArmour, pWeapon, eWeapon);
            playerTurn = !playerTurn; // toggle turns
            System.out.println(playerTurn ? "Player's Turn" : "Enemy's Turn");
        }  // while()

        ink.printGameOver(winner);
    } // main()

    // Helper Methods
    public static void createWarrior(String who, String playerName, int choice) {
        if (who.equals(playerName)) {
            switch (choice) {
                case 1: // Human
                    player = new Human("Human");
                    break;
                case 2: // Elf
                    player = new Elf("Elf");
                    break;
                case 3: // Orc
                    player = new Orc("Orc");
                    break;
                default:
                    System.out.println("oops!");
                    break;
            } // switch
        } else {
            switch (choice) {
                case 1: // Human
                    enemy = new Human("Human");
                    break;
                case 2: // Elf
                    enemy = new Elf("Elf");
                    break;
                case 3: // Orc
                    enemy = new Orc("Orc");
                    break;
                default:
                    System.out.println("oops!");
                    break;
            } // switch
        }
    } // createWarrior()    

    public static void createWeapon(String who, int choice) {
        int criticalChance; // Declare the variable without assigning a value yet
    
        switch (choice) {
            case 1: // Dagger
                criticalChance = 25; // Assign a value based on weapon type
                if (who.equals("Player")) {
                    pWeapon = new Dagger(criticalChance);
                } else {
                    eWeapon = new Dagger(criticalChance);
                }
                break;
            case 2: // Sword
                criticalChance = 10; // Assign a value based on weapon type
                if (who.equals("Player")) {
                    pWeapon = new Sword(criticalChance);
                } else {
                    eWeapon = new Sword(criticalChance);
                }
                break;
            case 3: // Axe
                criticalChance = 5; // Assign a value based on weapon type
                if (who.equals("Player")) {
                    pWeapon = new Axe(criticalChance);
                } else {
                    eWeapon = new Axe(criticalChance);
                }
                break;
            default:
                System.out.println("Invalid weapon choice!");
                break;    
        } // switch
    } // createWeapon()

    public static void createArmour(String who, int choice) {
        switch (choice) {
            case 1: // Leather
                if (who.equals("Player")) {
                    pArmour = new Leather();
                } else {
                    eArmour = new Leather();
                }
                break;
            case 2: // Chainmail
                if (who.equals("Player")) {
                    pArmour = new Chainmail();
                } else {
                    eArmour = new Chainmail();
                }
                break;
            case 3: // Platemail
                if (who.equals("Player")) {
                    pArmour = new Platemail();
                } else {
                    eArmour = new Platemail();
                }
                break;
            default:
                System.out.println("oops!");
                break;
        } // switch
    } // createArmour()

    public static void createWeather(int weatherType) {
        switch (weatherType) {
            case 1: // sun
                weather = new Sun();
                break;
            case 2: // rain
                weather = new Rain();
                break;
            case 3: // wind
                weather = new Wind();
                break;
            case 4: // storm
                weather = new Storm();
                break;
            default:
                System.out.println("Run!! Godzilla!!!");
                break;
        } // switch
    } // createWeather()
} // class
