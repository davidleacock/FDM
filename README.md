# Functional Domain Modelling

## This is just a rough collection of design notes and ideas


Using this repo to explore and play around with Functional Domain Modeling.
This will eventually get fleshed out further in a scalable distributed system

<b>Algebra</b>:
In FP an algebra is used to describe a set of operations, laws and types that form the essence
or semantics of a domain.  This is an interface (or trait) that defines the 
main operations that can be performed on or within that domain, without specify *how* these operations
are implemented

<b>TODO!🧠 </b> The benefit of higher kinded types in algebra design

Current design experiment for Device is using an F-Bounded polymorphism. So any class that extends
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