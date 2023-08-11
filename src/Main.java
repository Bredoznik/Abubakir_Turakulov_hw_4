import java.util.Random;

public class Main {
    public static int bossHealth = 5000;
    public static int bossDamage = 50;
    public static String bossWeak;
    public static String[] heroesNames = {"Warrior  ", "Mage     ", "Archer   ", "Cleric   ", "Witcher  ", "Lucky    ", "Thor     ", "Golem    "};
    public static String[] heroesAttackType = {"Physical", "Magic", "Range", "No weakness", "Silver", "Achilles' heel", "Thunder", "Push"};
    public static int[] heroesHealth = {300, 250, 280, 240, 250, 210, 300, 500};
    public static int[] heroesDamage = {30, 50, 40, 0, 30, 20, 30, 10};
    public static boolean[] luckyEvade = {false, false, false, false, false, false, false, false};
    public static int roundNumber;
    public static boolean witcherRevived = false;

    public static void main(String[] args) {
        showStatistics();
        while (!isGameOver()) {
            playRound();
        }
    }

    public static void thorStun() {
        Random random = new Random();
        if (random.nextBoolean()) {
            System.out.println("Boss stunned");
            System.out.println(" ");
            skipBossRound = true;
        } else {
            boolean anyHeroAlive = false;
            for (int health : heroesHealth) {
                if (health > 0) {
                    anyHeroAlive = true;
                    break;
                }
            }
            if (anyHeroAlive) {
                System.out.println("Boss attack");
                System.out.println(" ");
            }
            skipBossRound = false;
        }
    }

    public static boolean skipBossRound = false;

    public static void playRound() {
        roundNumber++;

        if (skipBossRound) {
            skipBossRound = false;
        } else {
            golemAbsorption();
            bossAttack();
        }

        heroesAttack();

        chooseBossWeak();
        thorStun();
        luckyEvade();
        witcherRevive();
        clericHeal();

        showStatistics();
    }

    public static void chooseBossWeak() {
        Random random = new Random();
        int randomIndex = -1;
        do {
            randomIndex = random.nextInt(heroesAttackType.length);
        } while (heroesDamage[randomIndex] <= 0);
        bossWeak = heroesAttackType[randomIndex];
    }

    public static void bossAttack() {
        int livingHeroesCount = 0;
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                livingHeroesCount++;
            }
        }
        int golemGroupDamage = 10 * (livingHeroesCount - 1);
        int golemDamage = + 10;

        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                if (heroesHealth[i] - bossDamage < 0) {
                    heroesHealth[i] = 0;
                } else {
                    heroesHealth[i] = heroesHealth[i] - bossDamage;
                }
            }
        }
        if (heroesHealth[7] > 0) {
            heroesHealth[7] -= golemDamage + golemGroupDamage;
            System.out.println("Golem take " + golemGroupDamage + " group damage");
            System.out.println(" ");
        }
    }

    public static void heroesAttack() {
        for (int i = 0; i < heroesDamage.length; i++) {
            if (heroesHealth[i] > 0 && bossHealth > 0) {
                int damage = heroesDamage[i];

                if (heroesAttackType[i] == bossWeak) {
                    Random random = new Random();
                    int crit = random.nextInt(9) + 2;
                    int critDamage = heroesDamage[i] * crit;

                    damage += critDamage;
                    System.out.println(critDamage + " critical damage");
                }

                if (bossHealth - damage < 0) {
                    bossHealth = 0;
                } else {
                    bossHealth -= damage;
                }
            }
        }
    }

    public static void golemAbsorption() {
        if (bossHealth > 0 && heroesHealth[7] > 0) {
            bossDamage = 40;
        } else {
            bossDamage = 50;
        }
    }

    public static void clericHeal() {
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0 && heroesHealth[i] < 100 && heroesNames[i].equals("Cleric   ")) {
                int targetIndex = chooseTargetToHeal(i);
                if (targetIndex != -1) {
                    Random random = new Random();
                    int healAmount = random.nextInt(100) + 100;
                    heroesHealth[targetIndex] += healAmount;
                    System.out.println(healAmount + " health given to " + heroesNames[targetIndex]);
                }
            }
        }
    }

    public static void witcherRevive() {
        if (!witcherRevived && heroesHealth[4] > 0) {
            for (int i = 0; i < heroesHealth.length; i++) {
                if (heroesHealth[i] <= 0 && heroesHealth[i] >= 0) {
                    heroesHealth[i] = heroesHealth[4];
                    witcherRevived = true;
                    heroesHealth[4] = 0;
                    System.out.println("Witcher revive " + heroesNames[i]);
                    break;
                }
            }
        }
    }

    public static void luckyEvade() {
        if (!luckyEvade[5] && heroesHealth[5] > 0) {
            Random random = new Random();
            int evadeChance = random.nextInt(100) + 1;
            if (evadeChance <= 50) {
                luckyEvade[5] = true;
                heroesHealth[5] += bossDamage;
                System.out.println("Lucky evade boss attack");
            }
        }
    }

    public static int chooseTargetToHeal(int clericIndex) {
        int target = -1;
        int minHealth = Integer.MAX_VALUE;

        for (int i = 0; i < heroesHealth.length; i++) {
            if (i != clericIndex && heroesHealth[i] > 0 && heroesHealth[i] < minHealth) {
                target = i;
                minHealth = heroesHealth[i];
            }
        }
        return target;
    }

    public static void showStatistics() {
        System.out.println(" ");
        System.out.println("-------------------- ROUND " + roundNumber + " --------------------");
        System.out.println(" ");
        System.out.println("Boss      Health: " + bossHealth + "      Damage: " + (bossDamage == 40 ? "50" : bossDamage) + "      Weakness: " + (bossWeak == null ? "No weakness" : bossWeak));
        System.out.println(" ");
        for (int i = 0; i < heroesHealth.length; i++) {
            System.out.println(heroesNames[i] + " Health: " + Math.max(heroesHealth[i], 0) + "      Damage: " + heroesDamage[i]);
        }
        System.out.println(" ");
    }

    public static boolean isGameOver() {
        if (bossHealth <= 0) {
            System.out.println("--------------------------------------------------");
            System.out.println("Win!");
            return true;
        }
        boolean allHeroesDead = true;
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                allHeroesDead = false;
                break;
            }
        }
        if (allHeroesDead) {
            System.out.println("--------------------------------------------------");
            System.out.println("Lose!");
        }
        return allHeroesDead;
    }
}