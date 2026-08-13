package myfunlaby

import com.funlabyrinthe.core.*
import com.funlabyrinthe.core.scene.*
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
  override def presentView(corePlayer: CorePlayer, viewSize: Size): SceneUpdateFragment =
    if corePlayer.reified[Player].position.exists(_.pos.z == 0) then
      super.presentView(corePlayer, viewSize)
    else
      SceneUpdateFragment.empty
end CatacombesViewRestrictionPlugin

class ClosedPorch(using ComponentInit) extends Obstacle:
  painter += "Gates/ClosedPorch"
end ClosedPorch

class Torch(using ComponentInit) extends Obstacle:
  painter += "Fields/Wall"
  painter += "Miscellaneous/TorchOn"

  override def pushing(context: EnteringContext): Unit = {
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
  override def buttonDown(context: EnteredContext): Unit = {
    import context.*

    enabled = false
    map(3, 1, 1) += goldenKey
    player.showMessage("Une clef d'or est déposée dans la maison voisine.")
  }
end KeyButton

class LightSwitch(using ComponentInit) extends Switch:
  override def switchOn(context: ExecuteContext): Unit = {
    import context.*

    player.plugins -= catacombesViewRestrictionPlugin
    player.showMessageOnce("Et la lumière fut...")
  }

  override def switchOff(context: ExecuteContext): Unit = {
    import context.*

    player.plugins += catacombesViewRestrictionPlugin
  }
end LightSwitch
