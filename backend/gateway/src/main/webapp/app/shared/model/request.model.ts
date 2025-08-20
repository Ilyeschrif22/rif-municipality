import dayjs from 'dayjs';
import { RequestStatus } from 'app/shared/model/enumerations/request-status.model';

export interface IRequest {
  id?: number;
  type?: string;
  description?: string;
  status?: keyof typeof RequestStatus;
  createdDate?: dayjs.Dayjs | null;
  resolvedDate?: dayjs.Dayjs | null;
  citizenId?: number | null;
  municipalityId?: number | null;
}

export const defaultValue: Readonly<IRequest> = {};
