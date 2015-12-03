package services

import doobie.imports.Transactor
import models.UserRepository
import services.messages._

import scalaz.concurrent.Task

trait UserServices {

  def getUserOrCreate(request: GetUserOrCreateRequest): Task[GetUserOrCreateResponse]

  def getUserByLogin(request: GetUserByLoginRequest): Task[GetUserByLoginResponse]

  def createUser(request: CreateUserRequest): Task[CreateUserResponse]

}

object UserServices {
  implicit def userServices(implicit userRepository: UserRepository, transactor: Transactor[Task]) : UserServices = new UserServicesImpl
}

class UserServicesImpl(implicit userRepository: UserRepository, transactor: Transactor[Task]) extends UserServices {

  override def getUserOrCreate(request: GetUserOrCreateRequest): Task[GetUserOrCreateResponse] =
    for {
      maybeUserResponse <- getUserByLogin(GetUserByLoginRequest(request.login))
      user <- maybeUserResponse.user match {
        case Some(u) => Task.now(u)
        case None => createUser(CreateUserRequest(
          login = request.login,
          name = request.name,
          github_id = request.github_id,
          picture_url = request.picture_url,
          github_url = request.github_url,
          email = request.email)) map (_.user)
      }
    } yield GetUserOrCreateResponse(user)


  override def getUserByLogin(request: GetUserByLoginRequest): Task[GetUserByLoginResponse] =
    transactor.trans(userRepository.getByLogin(login = request.login) map GetUserByLoginResponse)


  override def createUser(request: CreateUserRequest): Task[CreateUserResponse] =
    transactor.trans(for {
      user <- userRepository.create(
        login = request.login,
        name = request.name,
        github_id = request.github_id,
        picture_url = request.picture_url,
        github_url = request.github_url,
        email = request.email)
    } yield CreateUserResponse(user = user))

}