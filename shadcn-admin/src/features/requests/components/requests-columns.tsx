import { ColumnDef } from '@tanstack/react-table'
import { cn } from '@/lib/utils'
import { Badge } from '@/components/ui/badge'
import { Checkbox } from '@/components/ui/checkbox'
import LongText from '@/components/long-text'
import { requestStatusTypes, requestTypeIcons } from '../data/data'
import { RequestRow } from '../data/schema'
import { DataTableColumnHeader } from './data-table-column-header'
import { useI18n } from '@/context/i18n-context'
import { DataTableRowActions } from './data-table-row-actions'

export const requestColumns: ColumnDef<RequestRow>[] = [
  {
    id: 'select',
    header: ({ table }) => (
      <Checkbox
        checked={
          table.getIsAllPageRowsSelected() ||
          (table.getIsSomePageRowsSelected() && 'indeterminate')
        }
        onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
        aria-label='Sélectionner tout'
        className='translate-y-[2px]'
      />
    ),
    meta: {
      className: cn(
        'sticky md:table-cell left-0 z-10 rounded-tl min-w-[48px] w-[48px]',
        'bg-background group-hover/row:bg-muted group-data-[state=selected]/row:bg-muted'
      ),
    },
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={(value) => row.toggleSelected(!!value)}
        aria-label='Sélectionner la ligne'
        className='translate-y-[2px]'
      />
    ),
    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: 'type',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title={'table.type'} />
    ),
    filterFn: (row, columnId, filterValue) => {
      const typeValue = String(row.getValue(columnId) ?? '').toLowerCase()
      if (Array.isArray(filterValue)) {
        if (filterValue.length === 0) return true
        return (filterValue as string[]).some((v) => String(v).toLowerCase() === typeValue)
      }
      if (typeof filterValue === 'string') {
        const q = filterValue.trim().toLowerCase()
        if (!q) return true
        return typeValue.includes(q)
      }
      return true
    },
    cell: ({ row }) => {
      const type: string = row.getValue('type')
      const requestType = requestTypeIcons.find(({ value }) => value === type.toUpperCase())
      return (
        <div className='flex items-center gap-x-2 min-w-[160px]'>
          {requestType?.icon ? (
            <requestType.icon size={16} className='text-muted-foreground shrink-0' />
          ) : null}
          <LongText className='max-w-56'>{type}</LongText>
        </div>
      )
    },
    meta: {
      className: cn(
        'drop-shadow-[0_1px_2px_rgb(0_0_0_/_0.1)] dark:drop-shadow-[0_1px_2px_rgb(255_255_255_/_0.1)] lg:drop-shadow-none',
        'bg-background group-hover/row:bg-muted group-data-[state=selected]/row:bg-muted',
        'sticky left-6 md:table-cell'
      ),
    },
    enableHiding: false,
  },
  {
    accessorKey: 'description',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title={'table.description'} />
    ),
    cell: ({ row }) => (
      <LongText className='max-w-[32rem]'>{row.getValue('description')}</LongText>
    ),
    meta: { className: 'min-w-[240px]' },
  },
  {
    accessorKey: 'status',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title={'table.status'} />
    ),
    cell: ({ row }) => {
      const status = row.original.status
      const badgeColor = requestStatusTypes.get(status)
      const { t } = useI18n()
      return (
        <div className='flex space-x-2 min-w-[120px]'>
          <Badge variant='outline' className={cn('capitalize', badgeColor)}>
            {status === 'PENDING' && t('status.PENDING')}
            {status === 'IN_PROGRESS' && t('status.IN_PROGRESS')}
            {status === 'RESOLVED' && t('status.RESOLVED')}
            {status === 'REJECTED' && t('status.REJECTED')}
          </Badge>
        </div>
      )
    },
    enableHiding: false,
    enableSorting: false,
  },
  {
    accessorKey: 'createdAt',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title={'table.createdAt'} />
    ),
    cell: ({ row }) => {
      const date = row.getValue('createdAt') as Date
      return <div className='min-w-[120px]'>{date.toLocaleDateString('fr-FR')}</div>
    },
  },
  {
    id: 'actions',
    cell: DataTableRowActions,
    meta: { className: 'min-w-[56px] w-[56px]' },
  },
]

export const requestColumnsCompact: ColumnDef<RequestRow>[] = [
  requestColumns.find((c) => (c as any).id === 'select')!,
  requestColumns.find((c) => (c as any).accessorKey === 'type')!,
  requestColumns.find((c) => (c as any).accessorKey === 'status')!,
  requestColumns.find((c) => (c as any).accessorKey === 'createdAt')!,
  requestColumns.find((c) => (c as any).id === 'actions')!,
].filter(Boolean) as ColumnDef<RequestRow>[]


