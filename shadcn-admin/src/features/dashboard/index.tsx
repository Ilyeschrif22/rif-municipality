import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { ThemeSwitch } from '@/components/theme-switch'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Info } from 'lucide-react'
import { useI18n } from '@/context/i18n-context'
import { Button } from '@/components/ui/button'
import { useQuery } from '@tanstack/react-query'
import { fetchMyRequests } from '@/lib/api'
import { requestListSchema, type RequestRow } from '@/features/requests/data/schema'
import { RequestsProvider } from '@/features/requests/context/requests-context'
import { RequestsTable } from '@/features/requests/components/requests-table'
import { requestColumnsCompact } from '@/features/requests/components/requests-columns'
import { useNavigate } from '@tanstack/react-router'
import { useAuthStore } from '@/stores/authStore'

function DashboardInner() {
  const { t } = useI18n()
  const navigate = useNavigate()
  const user = useAuthStore((s) => s.auth.user)
  const { data } = useQuery({
    queryKey: ['requests', 'mine'],
    queryFn: async () => {
      const list = await fetchMyRequests()
      const mapped: RequestRow[] = list.map((r) => ({
        id: String(r.id ?? ''),
        type: r.type,
        description: r.description,
        status: r.status,
        createdAt: r.createdDate ? new Date(r.createdDate) : new Date(),
        updatedAt: r.resolvedDate ? new Date(r.resolvedDate) : new Date(),
      }))
      const parsed = requestListSchema.safeParse(mapped)
      return parsed.success ? parsed.data : mapped
    },
  })

  return (
    <>
      <Header>
        <div className='ml-auto flex items-center space-x-4'>
          <Search />
          <ThemeSwitch />
          <ProfileDropdown />
        </div>
      </Header>

      <Main>
        <div className='mb-2 flex items-center justify-between space-y-2'>
          <div>
            <h1 className='text-2xl font-bold tracking-tight'>{t('dashboard.title')}</h1>
          </div>
          <div className='flex items-center space-x-2'>
            <Button onClick={() => navigate({ to: '/requests/new' })}>{t('actions.newRequest')}</Button>
          </div>
        </div>

        {/* Welcome panel */}
        <Card className='mb-6 border-primary/30 bg-primary/5'>
          <CardContent className='py-0.5 px-2'>
            <div className='flex items-center gap-2 text-primary'>
              <Info className='h-3 w-3' />
              <Badge variant='secondary' className='h-3.5 px-1.5 py-0 border-primary/30 bg-primary/10 text-primary text-[9px]'>
                {t('welcome.badge')}
              </Badge>
              {(() => {
                const name = (user?.firstName && user?.lastName)
                  ? `${user.firstName} ${user.lastName}`
                  : (user?.email || 'Utilisateur')
                const template = t('welcome.line')
                const parts = template.split('{name}')
                return (
                  <span className='text-[13px] leading-tight font-normal text-primary'>
                    {parts[0]}
                    <span className='font-semibold'>{name}</span>
                    {parts[1] ?? ''}
                  </span>
                )
              })()}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>{t('dashboard.myRequests')}</CardTitle>
            <CardDescription>{t('dashboard.myRequestsDesc')}</CardDescription>
          </CardHeader>
          <CardContent>
            <RequestsTable data={data ?? []} columns={requestColumnsCompact} />
          </CardContent>
        </Card>
      </Main>
    </>
  )
}

export default function Dashboard() {
  return (
    <RequestsProvider>
      <DashboardInner />
    </RequestsProvider>
  )
}
