# Functional Domain Modelling

## This is just a rough collection of design notes and ideas


Using this repo to explore and play around with Functional Domain Modeling.
This will eventually get fleshed out further in a scalable distributed system.  Keeping this as a log of my 
thoughts, design ideas and philosophy.  I'll use a straight-line to seperate ideas

***

<b>Algebra</b>:
In FP an algebra is used to describe a set of operations, laws and types that form the essence
or semantics of a domain.  This is an interface (or trait) that defines the 
main operations that can be performed on or within that domain, without specify *how* these operations
are implemented

***
<b>Domain-Driven Design</b>:
<br>    
`Aggregate`: groups of domain entities that are treated as a single unit for data changes. <br><br>
`Entities`: object that have distinct identity. Its state is mutable, but its ID is immutable. <br><br>
`Value Object`: Objects that describe some characteristic but carry not concept of identity. <br><br>
`SmartHome` is the Aggregate root and an Entity.  `Device` is an Entity.  
We control and access Devices through the SmartHome. Our `SmartHomeRepository` should reflect this. We load
SmartHomes from the repo, perform operations on it and then save it back in. This enforces that all business rules are
enforced by the aggregate
<br>
<br>
<b>DDD and Repo Design</b>:<br><br>
Here is the first pass at my repo design which I have improved upon.  
```
trait SmartHomeRepository[F[_]] {
  def create(ownerInfo: ContactInfo): F[Either[RepositoryError, SmartHome]]
  def addThermostat(homeId: UUID, thermostat: Thermostat): F[Either[RepositoryError, SmartHome]]
  def addLight(homeId: UUID, lightSwitch: LightSwitch): F[Either[RepositoryError, SmartHome]]
  def addMotionDetector(homeId: UUID, motionDetector: MotionDetector): F[Either[RepositoryError, SmartHome]]
  def getHome(homeId: UUID): F[Either[RepositoryError, SmartHome]]
  def updateSmartHome[A <: Device[A]](device: A, smartHome: SmartHome): F[Either[RepositoryError, SmartHome]]
}
```
This was my first pass at it and served fine as I was fleshing out the  `SmartHomeService` algebra. However after further design
in that API I realized there was a bit of a mess between the two. Given what we know about DDD it's obvious that this repo is
breaking encapsulation but allowing changes to be made directly to internal parts, bypassing the aggregate root. This device
specific logic should belong in a domain service layer. 
```
trait SmartHomeRepository[F[_]] {
  def create(smartHome: SmartHome): F[Either[RepositoryError, SmartHome]]
  def update(smartHome: SmartHome): F[Either[RepositoryError, SmartHome]]
  def retrieve(homeId: UUID): F[Either[RepositoryError, SmartHome]]
}
```

***
### The benefit of higher kinded types in algebra design

Notice how the algebras are defined using higher-kinded types, i.e
```
trait SmartHomeService[F[_]]
```
Using this HKT it allows us to abstract over type constructors that contain or produce other types.  Initially we may design
the service knowing that it's going to exist over a network and our api calls must be async so it would be natural to design our API calls
with `Future` in mind as the return type, i.e
```
trait Service {
  def doSomething: Future[Result]
}
```
However when it comes to writing unit tests and testing the contract of our service we are forced to bring in the Future runtime and all its overhead (threadpools, execution context etc) 
which can result in slow tests or just a test suite with a lot of infrastructure. <br>
By using a HKT we it comes to implement our service we can restrict it to `F[_]: Monad` and use something really  light and fast like `IO` then our 
service that we write a test with looks like this
```
 trait Service[F[_]: Monad] {
  def doSomething: F[Result]
 }


class ServiceImpl extends Service[IO] {
  def doSomething: IO[Result]
}
```
This way im not tied down to what runtime executes this method. I can use something very light and just focus on testing the contract



***
Current design experiment for Device is using an `F-Bounded polymorphism`. So any class that extends
`Device` must do so in a way that specifics itself as the parameter for `Device`.  Where this comes into play
is the update() method in the device. 

```
trait Device {
  ...
  def update(other: A): A
}
```

When I extend this trait in something like a LightSwitch I want to be able to use LightSwitch as the parameters
in the method
```
def update(lightSwitch: LightSwitch) LightSwitch
```
In order to do this I can make use of `F-Bounded Polymorphism`
```
trait Device[A <: Device[A]] 

case class LightSwitch(...) extends Device[LightSwitch]
```

<b>TODO!🧠 </b>However from reading about this it seems like TypeClasses are much cleaner way to do so, which is something I'll explore further

<br>

### Kleisli Arrows
<b>TODO!🧠 </b> The SmartHome algebra makes use of these as the return type,  will add notes on why this is shortly..



<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>


## <b>TODOs 🧠</b> :
  - ~~Finish InMemory repo~~
  - ~~Implement the algebra APIs and flesh them out further~~
  - ~~Wire up repo to the service, kleisli arrow~~
  - Replace F-bounded with type classes
  - Create a way to call the APIs
  - ~~Organize modules~~
  - Investigate State monad for SmartHomeServiceImpl
  - Create device impl that mimic read-world devices (random data etc)
  - Finish tests for SmartHomeServiceImpl
  - Logging
  - Review domain concepts/api/repo, make sure it aligns
  - Device Rule engine? 