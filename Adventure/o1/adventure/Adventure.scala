package o1.adventure

import o1.sound.sampled._

/** The class `Adventure` represents text adventure games. An adventure consists of a player and
  * a number of areas that make up the game world. It provides methods for playing the game one
  * turn at a time and for checking the state of the game.
  *
  * N.B. This version of the class has a lot of "hard-coded" information which pertain to a very
  * specific adventure game that involves a small trip through a twisted forest. All newly created
  * instances of class `Adventure` are identical to each other. To create other kinds of adventure
  * games, you will need to modify or replace the source code of this class. */
class Adventure {

  /** The title of the adventure game. */
  val title = "Ghost Hunt"

  private var previousReport = ""
  private var triggerEnding = false
  private val ghostgroan = Sound("ghostgroan.wav")
  private val explosion = Sound("explosion.wav")
  private val river = Sound("river.wav")
  private val doorCreak = Sound("churchdoor.wav")


  private val oldMarketSquare = new Area("Old Market Square", "A huge empty landing area for your adventure.\nThis must have been a crowded market years ago.")
  private val mainRoad1 = new Area("Main Road", "You see a long road ahead. \nThere's ash fallout in the air.")
  private val mainRoad2 = new Area("Main Road", "There are buildings all around you.")
  private val mainRoad3 = new Area("Main Road", "Approaching the end of the road. You can hear a river in the north.")
  private val mainRoad4 = new Area("Main Road", "There is a collapsed bridge across the river. Must find another way to cross to the other side.")
  private val libruary = new Area("Libruary", "You walk inside the library and start looking for recourses. It's freezing cold.\nGhosts have definately been here recently.")
  private val cafe = new Area("Café", "You step into an old cafe. All the tables and counters are covered with thick layer of dust.")
  private val pharmacy = new Area("Pharmacy", "You are surrounded by empty shelves. You search for anything usable.")
  private val church = new Area("Church", "You look around the church. Pews and the altar are in shambles. \nRays of light shine down from the broken roof.\nThere is a door to west you can use to get back to the main road.")
  private val ruins = new Area("Ruins", "Old ruined building. Roof looks to be torn off and the walls are crumbling.\nAsh everywhere.\nYou see a small hole on the northern wall. When you peek through and see a boat at a dock.\nThere has to be a way to break this wall...")
  private val parkingLot = new Area("Parking lot", "The asphalt has been cracked and weeds grow through. Few abandoned cars with moss covering them.")
  private var dock = new Area("Dock", "You can see a dock on the river.\nThere is also a rowing boat you could use to cross the river.")

  private val downStream = new Area("Downstream", "The way back to the other side of the river is too risky.\nThe only way is forward.")
  private val ruinedRoad = new Area("Ruined Road", "This crossroads seems to have suffered from an earthquake.\nThe road has deep cracks and pavement collapsed.")
  private val graveyard = new Area("Graveyard", "Tombstones everywhere. It seems like someone has rearranged them to be closer to each other. Most of them are fallen over.\nMany candles are lit. How is that possible?")
  private val northBridge = new Area("Bridge", "Northside of the bridge. You can see the huge meteor in the north.")
  private val northEdge = new Area("Edge of the bridge", "You can see the main road and the buildings in the south. The Sun is rising.")
  private val meteor = new Area("The Meteor", "The Objective of your mission. The meteor is glowing with faint orange energy.\nDo what you came here to do.")

  private val destination = meteor

  oldMarketSquare.setNeighbors(Vector("north" -> mainRoad1))
  mainRoad1.setNeighbors(Vector("north" -> mainRoad2, "east" -> parkingLot, "south" -> oldMarketSquare, "west" -> libruary))
  mainRoad2.setNeighbors(Vector("north" -> mainRoad3, "south" -> mainRoad1, "west" -> cafe))
  mainRoad3.setNeighbors(Vector("north" -> mainRoad4, "east" -> ruins, "south" -> mainRoad2, "west" -> pharmacy))
  mainRoad4.setNeighbors(Vector("south" -> mainRoad3))
  libruary.setNeighbors(Vector("east" -> mainRoad1))
  cafe.setNeighbors(Vector("east" -> mainRoad2))
  pharmacy.setNeighbors(Vector("east" -> mainRoad3))

  ruins.setNeighbors(Vector("west" -> mainRoad3))

  dock.setNeighbors(Vector("south" -> ruins))

  church.setNeighbors(Vector("south" -> parkingLot, "west" -> mainRoad2))
  parkingLot.setNeighbors(Vector("north" -> church, "west" -> mainRoad1))
  downStream.setNeighbors(Vector("north" -> ruinedRoad))
  northBridge.setNeighbors(Vector("north" -> meteor, "east" -> ruinedRoad, "south" -> northEdge))
  northEdge.setNeighbors(Vector("north" -> northBridge))
  graveyard.setNeighbors(Vector("west" -> ruinedRoad))
  ruinedRoad.setNeighbors(Vector("west" -> northBridge, "east" -> graveyard))
  meteor.setNeighbors(Vector("south" -> northBridge))

  cafe.addEnemy(new Enemy("ghost"))
  ruins.addEnemy(new Enemy("ghost"))
  graveyard.addEnemy(new Enemy("ghost"))
  northEdge.addEnemy(new Enemy("ghost"))

  pharmacy.addItem(new Item("dynamite", "A single dynamite, not much of a blast."))
  church.addItem(new Item("lighter", "A regular lighter."))
  mainRoad4.addItem(new Item("paddle", "Could be useful with a rowing boat."))
  graveyard.addItem(new Item("stabilizer", "A strange device with unknown purpose."))


  /** The character that the player controls in the game. */
  val player = new Player(oldMarketSquare)

  /** The number of turns that have passed since the start of the game. */
  var turnCount = 0
  /** The maximum number of turns that this adventure game allows before time runs out. */
  val timeLimit = 60


  /** Determines if the adventure is complete, that is, if the player has won. */
  def isComplete = {
    this.player.location == this.destination && triggerEnding
  }

  /** Determines whether the player has won, lost, or quit, thereby ending the game. */
  def isOver = this.isComplete || this.player.hasQuit || this.turnCount == this.timeLimit || !player.isAlive

  /** Returns a message that is to be displayed to the player at the beginning of the game. */

  def welcomeMessage = "\nThe year is 2122. Earth has been infested with Ghosts, an alien species from outerspace.\nThey arrived with meteors that struck the earth 5 years ago." +
    "\nYou are tasked to go and destroy one of these meteors that work as a some sort of powersource for the aliens.\nEquipped with an Anti-Ghost Gun, your journey through hordes of Ghosts in a forgotten city begins..." +
    "\nHeadphones and command \"help\" are recommended."


  /** Returns a message that is to be displayed to the player at the end of the game. The message
    * will be different depending on whether or not the player has completed their quest. */
  def goodbyeMessage = {
    if (this.isComplete) {
      o1.play("[82]cccc--<g#--a#-->c <a#>c----/180")
      "Mission accomplished... Humanity is safe for now.\nTime to head home."
    } else if (this.turnCount == this.timeLimit) {
      ghostgroan.play(0,0f)
      Thread.sleep(1500)
      "You hear a huge mass of ghosts exploding out of the meteor. They are heading right towards you. There is nothing you can do." +
        "\nThe ghosts overpower you and impale you with their glowing orange seethrough tentacles.\nGame over!"
    } else if (!player.isAlive) {

      "The ghost effortlessly impales your armor with it's spectral tentacles. Pain is excruciating. You fall to the ground. The adventure is over..."
    } else // game over due to player quitting
      "Quitter!"
  }

  /** Plays a turn by executing the given in-game command, such as "go west". Returns a textual
    * report of what happened, or an error message if the command was unknown. In the latter
    * case, no turns elapse. */
  def playTurn(command: String) = {
    val action = new Action(command)

    /** Activates the sound effect when player advances from parking lot to the church. */
    if(previousReport.contains("asphalt")) {
      if(command == "go north") {
        doorCreak.play(0,-2.0f)
      }
    }

    /** Checks the previous area full description for ghost.
      * If area has a ghost and player doesn't kill it, the ghost kills the player.
      * This can be countered by using command shoot ghost. */
    if (previousReport.contains("Warning:")) {
      if (command != "shoot ghost") {
        player.isAlive = false
        ghostgroan.play(0,0f)
        Thread.sleep(1500)
      }
    }

    /** With area full description containing Objective,
      * the player can end the game by command use stabilizer. */
    if (previousReport.contains("The Objective")) {
      if (player.inventory.contains("stabilizer") && command == "use stabilizer")
        triggerEnding = true
    }

    /** Player can open a way through the ruins with a dynamite and a lighter
      * if he/she has collected them */
    if (previousReport.contains("Old ruined building")) {
      if (player.inventory.contains("dynamite") && player.inventory.contains("lighter") && command == "use dynamite") {
        ruins.setNeighbors(Vector("west" -> mainRoad3, "north" -> dock))
        ruins.description = "Old ruined building. Roof looks to be torn off and the walls are crumbling.\nDust everywhere.\nYou can now go through the wall to north."
        println("3...")
        Thread.sleep(1000)
        println("2...")
        Thread.sleep(1000)
        println("1...")
        Thread.sleep(1000)
        explosion.play(0,-5.0f)
      }
    }

    /** By checking that player has the paddle and is located in the dock,
      * player can use row command to venture forward across the river. */
    if (previousReport.contains("dock")) {
      if (player.inventory.contains("paddle") && command == "row") {
        dock.setNeighbors(Vector("north" -> downStream, "south" -> ruins))
        river.play(0,-3.0f)
        Thread.sleep(1000)
      }
    }

    val outcomeReport = action.execute(this.player)

    if (outcomeReport.isDefined) {
      this.turnCount += 1
      previousReport = player.location.fullDescription
    }


    outcomeReport.getOrElse("Unknown command: \"" + command + "\".")
  }

}

