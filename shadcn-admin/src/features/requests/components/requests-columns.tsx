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
        aria-label="Sélectionner tout"
        className="translate-y-[2px]"
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
        aria-label="Sélectionner la ligne"
        className="translate-y-[2px]"
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
    cell: ({ row }) => {
      const type: string = row.getValue('type')
      const requestType = requestTypeIcons.find(({ value }) => value === type.toUpperCase())
      return (
        <div className="flex items-center gap-x-2 min-w-[160px]">
          {requestType?.icon ? (
            <requestType.icon size={16} className="text-muted-foreground shrink-0" />
          ) : null}
          <LongText className="max-w-56">{type}</LongText>
        </div>
      )
    },
    meta: { className: 'sticky left-6 md:table-cell' },
    enableHiding: false,
  },
  {
    accessorKey: 'description',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title={'table.description'} />
    ),
    cell: ({ row }) => (
      <LongText className="max-w-[32rem]">{row.getValue('description')}</LongText>
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
        <div className="flex space-x-2 min-w-[120px]">
          <Badge variant="outline" className={cn('capitalize', badgeColor)}>
            {status === 'PENDING' && t('status.PENDING')}
            {status === 'IN_PROGRESS' && t('status.IN_PROGRESS')}
            {status === 'RESOLVED' && t('status.RESOLVED')}
            {status === 'REJECTED' && t('status.REJECTED')}
          </Badge>
        </div>
      )
    },
    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: 'createdAt',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title={'table.createdAt'} />
    ),
    cell: ({ row }) => {
      const date = row.getValue('createdAt') as Date
      return <div className="min-w-[120px]">{date.toLocaleDateString('fr-FR')}</div>
    },
  },
  {
    id: 'actions',
    cell: ({ row }) => <DataTableRowActions row={row} />,
    meta: { className: 'min-w-[56px] w-[56px]' },
  },
]

// Compact variant currently mirrors the full columns
export const requestColumnsCompact = requestColumns
