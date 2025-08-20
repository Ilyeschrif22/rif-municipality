import { HTMLAttributes, useState } from 'react'
import { z } from 'zod'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useNavigate, useSearch } from '@tanstack/react-router'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { PasswordInput } from '@/components/password-input'
import { useAuthStore } from '@/stores/authStore'
import axios from 'axios'
import { api } from '@/lib/api'

type UserAuthFormProps = HTMLAttributes<HTMLFormElement>

const formSchema = z.object({
  username: z.string().min(1, 'Please enter your username'),
  password: z
    .string()
    .min(1, 'Please enter your password')
    .min(7, 'Password must be at least 7 characters long'),
})

export function UserAuthForm({ className, ...props }: UserAuthFormProps) {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()
  const search = useSearch({ from: '/(auth)/sign-in' }) as { redirect?: string }
  const { setAccessToken, setUser } = useAuthStore((s) => s.auth)

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: '',
      password: '',
    },
  })

  async function onSubmit(data: z.infer<typeof formSchema>) {
    setIsLoading(true)
    setError(null)
    try {
      const res = await api.post(
        '/api/authenticate',
        {
          username: data.username,
          password: data.password,
          rememberMe: true,
        }
      )
      const token: string | undefined = res.data?.id_token
      if (!token) throw new Error('Invalid response from server')
      setAccessToken(token)
      // Fetch authenticated account details
      try {
        const accRes = await api.get('/api/account')
        const acc = accRes.data as any
        setUser({
          accountNo: acc?.login ?? '',
          email: acc?.email ?? '',
          firstName: acc?.firstName ?? '',
          lastName: acc?.lastName ?? '',
          role: Array.isArray(acc?.authorities) ? acc.authorities : [],
          exp: 0,
        })
      } catch {
        setUser({ accountNo: '', email: '', firstName: 'Utilisateur', lastName: '', role: [], exp: 0 })
      }
      const to = search?.redirect || '/'
      navigate({ to })
    } catch (e: any) {
      setError(e?.response?.data?.message || 'Authentication failed')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className={cn('grid gap-3', className)}
        {...props}
      >
        <FormField
          control={form.control}
          name='username'
          render={({ field }) => (
            <FormItem>
              <FormLabel>Username</FormLabel>
              <FormControl>
                <Input placeholder='admin or user' {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name='password'
          render={({ field }) => (
            <FormItem className='relative'>
              <FormLabel>Password</FormLabel>
              <FormControl>
                <PasswordInput placeholder='********' {...field} />
              </FormControl>
              <FormMessage />
              <Link
                to='/forgot-password'
                className='text-muted-foreground absolute -top-0.5 right-0 text-sm font-medium hover:opacity-75'
              >
                Forgot password?
              </Link>
            </FormItem>
          )}
        />
        <Button className='mt-2' disabled={isLoading}>
          Login
        </Button>
        {error && (
          <p className='text-sm text-red-600 mt-2'>{error}</p>
        )}

      </form>
    </Form>
  )
}
