package myfunlaby

import com.funlabyrinthe.core.*
import com.funlabyrinthe.core.graphics.*
import com.funlabyrinthe.mazes.*
import com.funlabyrinthe.mazes.std.*

import user.sjrd.viewrestriction.*

object Catacombes extends Module

@definition def catacombesViewRestrictionPlugin(using Universe) = new CatacombesViewRestrictionPlugin
@definition def closedPorch(using Universe) = new ClosedPorch
@definition def torch(using Universe) = new Torch
@definition def keyButton(using Universe) = new KeyButton
@definition def lightSwitch(using Universe) = new LightSwitch

class CatacombesViewRestrictionPlugin(using ComponentInit) extends ViewRestrictionPlugin:
  override def drawView(corePlayer: CorePlayer, context: DrawContext): Unit =
    if corePlayer.reified[Player].position.exists(_.pos.z == 0) then
      super.drawView(corePlayer, context)
  end drawView
end CatacombesViewRestrictionPlugin

class ClosedPorch(using ComponentInit) extends Obstacle:
  painter += "Gates/ClosedPorch"
end ClosedPorch

class Torch(using ComponentInit) extends Obstacle:
  painter += "Fields/Wall"
  painter += "Miscellaneous/TorchOn"

  override def pushing(context: MoveContext): Unit = {
    import context.*

    cancel()

    if keyEvent.isDefined then
      pos() = wall
      player.showMessage("Tu as trouvé une torche. Tu pourras mieux voir dans les catacombes.")

      if player.plugins.contains(catacombesViewRestrictionPlugin) then
        while player.attributes(viewRestrictionRadius) < 90 do
          player.attributes(viewRestrictionRadius) += 2
          player.sleep(100)
      else
        player.attributes(viewRestrictionRadius) = 90
    end if
  }
end Torch

class KeyButton(using ComponentInit) extends PushButton:
  override def buttonDown(context: MoveContext): Unit = {
    import context.*

    enabled = false
    pos.map(3, 1, 1) = pos.map(3, 1, 1) + goldenKey
    player.showMessage("Une clef d'or est déposée dans la maison voisine.")
  }
end KeyButton

class LightSwitch(using ComponentInit) extends Switch:
  var messageShown: Boolean = false

  override def switchOn(context: MoveContext): Unit = {
    import context.*

    player.plugins -= catacombesViewRestrictionPlugin

    if !messageShown then
      player.showMessage("Et la lumière fut...")
      messageShown = true
  }

  override def switchOff(context: MoveContext): Unit = {
    import context.*

    player.plugins += catacombesViewRestrictionPlugin
  }
end LightSwitch
