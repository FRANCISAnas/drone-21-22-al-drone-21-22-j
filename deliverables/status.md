### S2 :


# Répartition des points + implication de chaque membre :

- ANAGONOU Sourou Patrick 100 points => (Gestion de changement des regions + La comparaison de Kafka vs AMPQ).

- ANIGLO Vihoalé Jonas 100 point => (Mise en place et synchronisation de REDIS).
- FRANCIS Anas 100 points => (Mise en place du Gateway + Maintenir le CI sur GitHub Actions).
- ZABOURDINE Soulaiman 100 points => (Mise en place du pattern circuit breaker sur les services afin de les rendre resilients).


# Semaine 08:

Choix des éléments à présenter pour le démonstration du vendredi 25/02.

Commencer la rédaction du rapport.


GREEN



# Semaine 07:

Comparatif fonctionnel et non-fonctionnel entre RabbitMQ et Kafka.

Début de gestion des livraisons en cas de non disponibilité des drones.

Commencer la préparation de la démonstration du vendredi 25/02.


GREEN


# Semaine 06:

Début d'implémentation du service __DeliveryTracking__ qui va permettre de donner le statut sur une livraison.

Implémentation de toutes les communications faites en RabbitMQ avec Kafka sur une nouvelle branche


GREEN


# Semaine 05:

Début de préparation d'une démonstration.

Correction du bug lié à l'integration du circuitBreaker et API Gateway.

Création d'une base de données par instance de __FlightMonitor__.

Début d'utilisation de Kafka.


GREEN


# Semaine 04:

Mise en place de l'API Gateway.

Renforcement de la resilience du service __flightMonitor__ et le __stationManager__ (avec resilience4j) en utilisant le pattern circuitBreaker.

Prototype de la redirection des flux vers les __flightMonitor__, en fonction du changement de la région.

Mise en place du pattern CQRS, et début d'un benchmarking de la base de donnée afin de déterminer les limites de l'implementation de ce pattern.

YELLOW



# Semaine 03:

Mise en place d'une API Gateway + créer 2 nouveaux service __Flightmonitor__ pour tester que l'API Gateway dirige correctement le flux vers le bon __FlightMonitor__.

Commencer à gérer le changement de la région avec un refactor du système embarqué du drone.

Commencer à ajouter des CircuitBreakers (avec Resilience4j) sur les requêtes du __FlightMonitor__.

Commencer à préparer la démonstation du vendredi 21/01.

Commencer à faire des recherches pour mettre en place le patterne CQRS en spring boot, et commencer à implémenter ce patterne dans le walking skeleton.

## Pour la semaine prochaine :

Ajout de la gestion de réorientation dans le API Gateway en fonction du body de la requête.


YELLOW




# Semaine 02 :

Dans un premier temps, afin d'étudier le besoin de chaque nouvelles tâches, nous avons créé sur GitHub une SPIKE et avons débuté notre analyse.

Nous avons ensuite travaillé sur une nouvelle organisation de notre architecture afin d'adapter l'application aux nouveaux besoins, particulièrement autour de notre FlightMonitor.

Grâce à cette nouvelle organisation, nous allons pouvoir alléger la charge du FlightMonitor en déléguant les requêtes 'Statut de la livraison' à un nouveau service __'DeliveryTracker'__.

Ensuite, rajouter un Gateway, qui va permettre de rediriger le traffic vers le bon FlightMonitor.

Les instances de FlightMonitor, vont accéder à la même base de données. Nous allons utiliser le design pattern CQRS, afin de gérer la montée en charge des évènements que les drones vont envoyer.

Nous avons mis à jour notre repository GitHub avec le CI de GitHub actions ainsi que les tests.

## Risques potentielles :

-	Trouver les bons stack technologique, afin d'implémenter le pattern CQRS ainsi que le Gateway.

-	La montée en charge du service __FlightMonitor__.


## Pour la semaine prochaine :

Ce qui nous reste à faire, c'est commencer à implémenter cette nouvelle architecture.


YELLOW



### S1 : 


# Répartition des points + implication de chaque membre :

- ANAGONOU Sourou Patrick 100 points => (Dockerisation des services + customer care service + communication entre les services).

- ANIGLO Vihoalé Jonas 100 point => (path finder algorithm + update drone embedded system + maintenance servuce).
- FRANCIS Anas 100 points => (drone embedded system + CI sur github + station manager service).
- ZABOURDINE Soulaiman 100 points => (Charging stations + flight plan refactor + update drone embedded system).


# Semaine 44:

Nous avons commencé la préparation pour la soutenance du 5 novembre. Nous allons présenter un scénario (walking skelton) qui va traverser toutes les couches de notre application. 

Une aide visuelle avec des codes couleurs et des logs seront présentés pendant la présentation pour faciliter la compréhension de notre implémentation.

## Risques potentiels:

- Perte de la connexion du drone avec le bus.


# Semaine 43:

Nous avons créé le système embedded du drone, et nous avons mis en place une chaîne d'intégration continue. Cette chaîne va uniquement compiler le projet et créer les images docker.

Nous avons aussi créer le service de maintenance qui va recevoir des notifications des drones mais il n'est pas branché avec les autres services.

Nous avons commencé à préparer les scénarios et les slides de la démonstration du 5 novembre.

**Risques**: Chargement d'un nouvel itinéraire quand une station de chargement tombe en panne à implémenter.

## Pour la semaine prochaine

Nous allons mettre au propre notre code et bien assurer l'exécution des différents scénarios.

GREEN


# Semaine 42:

Nous avons mis en place la majorité des parties de notre application (CustomerCareService, DeliveryPlanner, FlightMonitor, ChargingStationManager et DroneEmbeddedSystem ) et la communication entre elles.

Nous avons donc un walking skeleton (milestone WEEK41) fonctionnel permettant de confier un paquet à livrer à notre application, de retrouver un entrepôt, un drone et des stations de recharge pour construire un chemin pour faire la livraison. 

Pour le moment notre drone répond immédiatement qu'il a fini la livraison dès qu'il a reçu la commande  de départ. Cette semaine nous allons travailler à simuler le fait qu'il suit effectivement le plan de vol reçu  ainsi que la gestion de scénarios de panne ou d'impossibilité de livraisons. (milestone WEEK42)

Comme risques nous n'avons pas encore mis en place une intégration continue automatisée sur le dépôt github.
Il est possible que l'arrangement de nos stations ainsi que de nos entrepôts rendent impossible la livraison de certaines demandes (Trajets trop distancés sans stations sur le chemin). Dans ces cas, nous supposons que nous avons la possibilité de déléguer la livraison à 1 autre entreprise (service externe) de livraison.

## Pour la semaine prochaine

Nous allons implémenter le WEEK43 (optimisation du chemin de livraison et gérer la livraison de plusieurs paquets en 1 seul parcours).

GREEN



# Semaine 41:

Nous avons commencé à implémenter la milestone WEEK41. Nous avons mis en place des outils d'automatisation pour compiler, lancer nos services pour s'assurer qu'ils sont bien connectés.


## Pour la semaine prochaine

Nous allons implémenter le WEEK 42

GREEN



# Semaine 40:

Nous avons effectué la construction du diagramme de composants ainsi que du diagramme de classes. Cela nous a permis d’avoir une vue poussée sur l’architecture de notre projet. Ces diagrammes nous ont également permis de quantifier les complexités de l’application, grâce à quoi nous avons pu réaliser une roadmap pour la livraison du MVP.

Nous avons également discuté de la stack technologique pour notre projet, et nous avons chosi de construire notre application en Java Spring Boot.

## Pour la semaine prochaine

Nous allons implementer le premier millestone WEEK41.

GREEN

# Semaine 39:

Nous avons commencé par définir le scope et les scénarios de notre projet tout en étant centré sur l'axe de notre variant V10bis ```"V10bis: battery management, charging dock networks with optimization (docking station are on the path of heavy drones for long-distance deliveries, "a la TESLA")"```

Les scénarios ainsi que la portée se trouvent dans le fichier architecture.pdf

Pour l'instant il n'y a pas de risques particuliers.


## Pour la semaine prochaine

Nous envisageons de faire le diagramme de composants pour montrer notre découpage. Et le roadmap pour montrer le planning du développement du projet.

Nous envisageon aussi à faire le choix de notre stack technologique afin de préparer le set-up sur le github classeroom pour commencer à implémenter les fonctionnalités dans les semaines qui suivent.

GREEN