package es.xeria.iventxff.domain.base


sealed class FailureError{
    /** Base Failure Errors */
    object Unauthorized: FailureError()
    object Mapping: FailureError()
    object Network: FailureError()
    object NotFound: FailureError()
    object Generic: FailureError()
    object BadRequest: FailureError()

    object NotBody: FailureError()

    object UserNotExist: FailureError()
    object EmailNotValidated: FailureError()

    object MapperError: FailureError()
}
