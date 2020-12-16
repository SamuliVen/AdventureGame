package o1.adventure

import scala.collection.mutable.Map


/** A `Player` object represents a player character controlled by the real-life user of the program.
  *
  * A player object's state is mutable: the player's location and possessions can change, for instance.
  *
  * @param startingArea the initial location of the player */
class Player(startingArea: Area) {

  private var alive = true
  private var currentLocation = startingArea // gatherer: changes in relation to the previous location
  private var quitCommandGiven = false // one-way flag
  private var pockets = Map[String, Item]()

  /** Determines if the player has indicated a desire to quit the game. */
  def hasQuit = this.quitCommandGiven

  var isAlive = alive

  /** Returns the current location of the player. */
  def location = this.currentLocation


  /** Attempts to move the player in the given direction. This is successful if there
    * is an exit from the player's current location towards the direction name. Returns
    * a description of the result: "You go DIRECTION." or "You can't go DIRECTION." */
  def go(direction: String) = {
    val destination = this.location.neighbor(direction)
    this.currentLocation = destination.getOrElse(this.currentLocation)
    if (destination.isDefined) "You go " + direction + "." else "You can't go " + direction + "."
  }

  /** Attempts to use a given item. The items have very specific conditions to be used successfully.
    * e.g. Dynamite needs a lighter to be used and can only be used in ruins. Stabilizer can only be used on the meteor.*/
  def use(itemName: String) = {
    if (itemName == "dynamite" && location.name == "Ruins" && pockets.contains("dynamite") && pockets.contains("lighter")) {
      pockets.remove("dynamite")
      "The wall in the ruins explodes and opens a path to the riverside."
    } else if (itemName == "dynamite" && pockets.contains("dynamite") && !pockets.contains("lighter")) {
      "You need something to light the dynamite."
    } else if (itemName == "stabilizer" && location.name == "The Meteor" && pockets.contains("stabilizer")) {
      pockets.remove(itemName)
      "You turn the stabilizer on and throw it to the meteor.\nThe orange glow on the meteor fades."
    } else if (itemName == "stabilizer" && location.name != "The Meteor") {
      "You are not supposed to use it here."
    } else if(pockets.contains(itemName) && itemName != "dynamite" && itemName != "stabilizer") {
      "How could I use this..."
    } else {
      "You don't have that!"
    }
  }

  def drop(itemName: String): String = {
    if (pockets.contains(itemName)) {
      location.addItem(pockets(itemName))
      pockets.remove(itemName)
      "You drop the " + itemName
    } else
      "You don't have that!"
  }


  def row(): String = {
    if (location.name == "Dock" && !pockets.contains("paddle")) {
      "I need something to row the boat with."
    } else if (location.name == "Dock" && pockets.contains("paddle")) {
      this.go("north")
      pockets.remove("paddle")
      "You row your boat across the river. Waves crash to your boats' sides and almost tip you over, but you stay onboard. Finally you reach the other side."
    } else if (location.name == "Downstream") {
      "Its too risky to go back."
    } else {
      "Why would I want to do that here?"
    }
  }

  def shoot(enemy: String): String = {
    if (currentLocation.containsGhost(enemy)) {
      currentLocation.killEnemy(enemy)
      "You vaporized the ghost into a cloud of ash. The cloud went with the wind.\nSomehow its heading towards the meteor."
    } else {
      "There is nothing to shoot at."
    }
  }

  def help: String = {
    "Commands:\n" +
      "go (direction)  : Control the character.\n" +
      "get (item)      : Pick up an item.\n" +
      "examine (item)  : Read item description.\n" +
      "row             : Command used to cross a certain area.\n" +
      "drop (item)     : Drop the item on the floor.\n" +
      "shoot ghost     : Shoot at a targetted ghost.\n" +
      "inventory       : Show your inventory.\n" +
      "quit            : Quit the game"
  }

  def examine(itemName: String): String = {
    if (pockets.contains(itemName)) {
      "You look closely at the " + itemName + ". \n" +
        pockets(itemName).description
    } else
      "If you want to examine something, you need to pick it up first."
  }

  def get(itemName: String): String = {
    if (location.contains(itemName)) {
      pockets += itemName -> location.removeItem(itemName).get
      (s"You pick up the $itemName.")
    }
    else
      (s"There is no $itemName here to pick up.")
  }

  def has(itemName: String): Boolean = {
    pockets.contains(itemName)
  }

  def inventory: String = {
    var inventory = ""
    if (pockets.isEmpty)
      "You are empty-handed."
    else {
      for (item <- pockets.keys) {
        inventory += "\n" + item
      }
      "You are carrying: " + inventory
    }

  }

  /** Signals that the player wants to quit the game. Returns a description of what happened within
    * the game as a result (which is the empty string, in this case). */
  def quit() = {
    this.quitCommandGiven = true
    ""
  }


  /** Returns a brief description of the player's state, for debugging purposes. */
  override def toString = "Now at: " + this.location.name


}


