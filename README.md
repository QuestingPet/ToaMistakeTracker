# Toa Mistake Tracker

Tracks mistakes made by players throughout the Tombs of Amascut.


---
This plugin will track mistakes for you and your teammates in the Tombs of Amascut.

By default, when detecting a mistake, all players with this plugin will receive a public message of the mistake, a chat
overhead above the player who made the mistake, and the mistake will be added to the Toa Mistake Tracker side-panel.

Current mistakes being tracked:

* ![death](src/main/resources/com/toamistaketracker/death.png) **Deaths** throughout the raid
    * ![death-akkha](src/main/resources/com/toamistaketracker/death-akkha.png) **Death in the Path of Het**
    * ![death-zebak](src/main/resources/com/toamistaketracker/death-zebak.png) **Death in the Path of Crondis**
    * ![death-kephri](src/main/resources/com/toamistaketracker/death-kephri.png) **Death in the Path of Scabaras**
    * ![death-baba](src/main/resources/com/toamistaketracker/death-baba.png) **Death in the Path of Apmeken**
    * ![death-wardens](src/main/resources/com/toamistaketracker/death-wardens.png) **Death in The Wardens**
* ![het-light](src/main/resources/com/toamistaketracker/het-light.png) **Het Puzzle energy beam** damage
* ![het-dark-orb2](src/main/resources/com/toamistaketracker/het-dark-orb2.png) **Het Puzzle dark orb** damage
* ![akkha-quadrant3](src/main/resources/com/toamistaketracker/akkha-quadrant3.png) **Akkha quadrant special attacks**
  damage
* ![akkha-elemental2](src/main/resources/com/toamistaketracker/akkha-elemental2.png) **Akkha elemental special attacks**
  damage
* ![akkha-unstable-orb](src/main/resources/com/toamistaketracker/akkha-unstable-orb.png) **Akkha unstable orb** damage
* ![crondis-water](src/main/resources/com/toamistaketracker/crondis-water.png) **Crondis Puzzle low watering**
* ![zebak-acid](src/main/resources/com/toamistaketracker/zebak-acid.png) **Zebak Acid** damage
* ![zebak-blood-cloud](src/main/resources/com/toamistaketracker/zebak-blood-cloud.png) **Zebak blood cloud** damage
* ![zebak-scream](src/main/resources/com/toamistaketracker/zebak-scream.png) **Zebak scream** damage
* ![zebak-wave](src/main/resources/com/toamistaketracker/zebak-wave.png) **Zebak wave** damage
* ![kephri-bomb](src/main/resources/com/toamistaketracker/kephri-bomb.png) **Kephri bomb** non-vengeance damage
* ![apmeken-sight](src/main/resources/com/toamistaketracker/apmeken-sight.png) **Apmeken Puzzle sight** team damage
* ![apmeken-venom](src/main/resources/com/toamistaketracker/apmeken-venom.png) **Apmeken Puzzle venom** damage
* ![apmeken-volatile](src/main/resources/com/toamistaketracker/apmeken-volatile.png) **Apmeken Puzzle volatile** damage
* ![baba-slam](src/main/resources/com/toamistaketracker/baba-slam.png) **Ba-Ba slam** damage
* ![baba-projectile-boulder](src/main/resources/com/toamistaketracker/baba-projectile-boulder.png) **Ba-Ba projectile
  boulder** damage
* ![baba-rolling-boulder](src/main/resources/com/toamistaketracker/baba-rolling-boulder.png) **Ba-Ba rolling boulder**
  damage
* ![baba-falling-boulder](src/main/resources/com/toamistaketracker/baba-falling-boulder.png) **Ba-Ba falling boulder**
  damage
* ![baba-banana](src/main/resources/com/toamistaketracker/baba-banana.png) **Ba-Ba banana** damage
* ![baba-gap](src/main/resources/com/toamistaketracker/baba-gap.png) **Ba-Ba gap** falling
* ![wardens-pyramid](src/main/resources/com/toamistaketracker/wardens-pyramid.png) **Wardens P1 pyramids** damage
* ![wardens-obelisk](src/main/resources/com/toamistaketracker/wardens-obelisk.png) **Wardens P2 obelisk special
  attacks** damage
* ![wardens-bind](src/main/resources/com/toamistaketracker/wardens-bind.png) **Wardens P2 bind** hit
* ![wardens-special-prayer](src/main/resources/com/toamistaketracker/wardens-special-prayer.png) **Wardens P2 special
  prayer attack** prayer miss
* ![wardens-earthquake](src/main/resources/com/toamistaketracker/wardens-earthquake.png) **Wardens P3 slam attack**
  damage
* ![wardens-akkha](src/main/resources/com/toamistaketracker/wardens-akkha.png) **Wardens P3 Akkha attack** prayer miss (
  no announcement)
* ![wardens-zebak](src/main/resources/com/toamistaketracker/wardens-zebak.png) **Wardens P3 Zebak attack** prayer miss (
  no announcement)
* ![wardens-kephri](src/main/resources/com/toamistaketracker/wardens-kephri.png) **Wardens P3 Kephri attack** damage
* ![wardens-baba](src/main/resources/com/toamistaketracker/wardens-baba.png) **Wardens P3 Ba-Ba attack** damage
* ![wardens-lightning](src/main/resources/com/toamistaketracker/wardens-lightning.png) **Wardens P3 lightning** damage (
  no announcement)

---

## Screenshots

![panel-example](src/main/resources/com/toamistaketracker/panel-example.png)

![death-example](src/main/resources/com/toamistaketracker/death-example.png)

![crondis-example](src/main/resources/com/toamistaketracker/crondis-example.png)

---

## Changes

#### 1.2

* Fixed Wardens P2 special prayer (divine projectile) attack counting as a mistake even if nulled from core being out

#### 1.1

* Fixed Akkha special attacks detecting a mistake when the player was not actually hit, due to a recent game change

#### 1.0

* Initial release