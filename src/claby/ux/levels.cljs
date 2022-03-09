(ns claby.ux.levels
  (:require [mzero.game.generation :as gg]))

(defonce levels
  [{:message
    {:en "Little rabbit must eat all the strawberries"
     :fr "Lapinette enceinte doit manger un maximum de fraises"}
    ::gg/density-map {:fruit 3
                      :cheese 0}
    :level-info [:div [:p "This is the first level. Just eat the fruits :)"]]}
   {:message {:fr "Attention au fromage non-pasteurisé !"
              :en "Unpasteurized cheese is dangerous for rabbits!"}
    ::gg/density-map {:fruit 3
                      :cheese 3}
    :message-color "darkgoldenrod"
    :level-info
    [:div
     [:p [:b "What's new? "] "This level introduces cheese."]
     [:p [:b "How hard is it for Machines?"] "The possibility to lose
   the game is something they have to account for. It's not that
   hard--although it's not as easily understood as humans do."]]}
   {:message {:en "Avoid alcoholic drinks"
              :fr "Evite les apéros alcoolisés"}
    ::gg/density-map {:fruit 5
                      :cheese 3}
    :message-color "darkblue"
    :enemies [:drink :drink :drink :drink]
    :level-info
    [:div
     [:p [:b "What's new? "] "This level introduces enemies."]
     [:p [:b "How hard is it for Machines?"]
      "They have to model enemy behaviour: be able to make
   guesses as to what the enemy will do next. For humans, this is
   innate, but not for AIs."]
     [:p "The enemy behaves simply so the game is still not very hard for machines yet."]]}
   {:message {:en "Mice run loose in the house!"
              :fr "Les souris ont infesté la maison!"}
    ::gg/density-map {:fruit 5
                      :cheese 3}
    :message-color "darkmagenta"
    :enemies [:mouse :mouse :mouse :mouse]
    :level-info
    [:div
     [:p [:b "What's new? "] "Enemies go faster."]
     [:p [:b "How hard is it for Machines?"]
      "In this case, adapting is simpler for machines
      than for humans. Faster enemies will make the game harder for
      us than for AIs."]]}
   {:message {:en "Covid shows up!"
              :fr "Le covid déboule!"}
    ::gg/density-map {:fruit 5
                      :cheese 3}
    :message-color "darkcyan"
    :enemies [:drink :mouse :virus :virus]
    :level-info
    [:div
     [:p [:b "What's new? "] "Multiple enemies with different speeds."]
     [:p [:b "How hard is it for Machines?"]
      "It is harder to model different kinds of enemies, so it's a
   little bit harder for machines (except for the fast ones, who
   won't care at all)"]
     [:p "The difficulty is still pretty standard to handle,
   though. Things will start getting tough for machines the level
   after."]]}
   {:message {:en "Foggy day"
              :fr "Un jour de brouillard"}
    ::gg/density-map {:fruit 5
                      :cheese 3}
    :message-color "darkcyan"
    :enemies [:drink :mouse]
    :rules [:fog-of-war]
    :level-info
    [:div
     [:p [:b "What's new? "] "Some stuff is hidden."]
     [:p [:b "How hard is it for Machines?"]
      "The real difficulties start now. Since some stuff is unknown,
      it is hard to program rules about what to do so rule-based
      machines will not handle this well. Simple learning
      machines will also struggle as soon as no strawberry is in sight anymore."]
     [:p ""]]}
   {:message {:en "Upside down"
              :fr "A l'envers"}
    ::gg/density-map {:fruit 5
                      :cheese 3}
    :message-color "darkcyan"
    :enemies [:drink :mouse]
    :rules [:controls-switch]
    :level-info
    [:div
     [:p [:b "What's new? "] "Controls change"]
     [:p [:b "How hard is it for Machines?"]
      "Any machine with little to no learning ability will fail
      instantly here. Even relatively sophisticated AIs will struggle."]]}
   {:message {:en "Success needs momentum"
              :fr "L'élan de la réussite"}
    ::gg/density-map {:fruit 5
                      :cheese 3}
    :message-color "darkcyan"
    :enemies [:drink :mouse]
    :rules [:momentum-rule]
    :level-info
    [:div
     [:p [:b "What's new? "] "A new weird rule appears."]
     [:p [:b "How hard is it for Machines?"]
      "The rule is not basic, so simple learning machines won't be able to
      find it out. More advanced ones will manage though. As usual,
      rule-based machines will fail unless somebody wrote in them the
      exact code for the rule."]
     [:p ""]]}
   #_{:message {:en "All right, let's raise the stakes."
              :fr "Allez on arrête de déconner."}
    ::gg/density-map {:fruit 5
                      :cheese 5}
    :message-color "darkgreen"
    :enemies [:drink :drink :virus :virus :mouse :mouse]}
   #_{:message {:en "Fake level"
                :fr "Fake level"}
      ::gg/density-map {:fruit 2
                        :cheese 0}
      :message-color "darkgreen"
      :enemies []}])
