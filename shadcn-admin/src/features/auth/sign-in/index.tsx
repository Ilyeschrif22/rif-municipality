import { Link } from '@tanstack/react-router'
import { UserAuthForm } from './components/user-auth-form'

export default function SignIn() {
  return (
    <div className="w-full lg:grid lg:min-h-screen lg:grid-cols-2">
      <div className="flex items-center justify-center py-12">
        <div className="mx-auto w-full max-w-md space-y-6 px-6">
          <div className="space-y-2 text-center">
            <h1 className="text-2xl font-bold tracking-tight">Se connecter</h1>
            <p className="text-muted-foreground">
              Entrez votre cin et votre mot de passe pour vous connecter.
            </p>
          </div>
          <UserAuthForm />
          <p className="px-8 text-center text-sm text-muted-foreground">
            Pas de compte ?{' '}
            <Link to="/sign-up" className="underline underline-offset-4 hover:text-primary">
              Créer un compte
            </Link>
          </p>
        </div>
      </div>
      <div className="hidden bg-muted lg:block">
        <img
          src="https://upload.wikimedia.org/wikipedia/commons/3/3c/Mairie_de_Tunis.jpg"
          alt="Mairie de Tunis"
          className="h-full w-full object-cover"
        />
      </div>
    </div>
  )
}
