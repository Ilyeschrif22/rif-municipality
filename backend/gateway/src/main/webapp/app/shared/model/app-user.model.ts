import dayjs from 'dayjs';

export interface IAppUser {
  id?: number;
  login?: string;
  firstName?: string | null;
  lastName?: string | null;
  email?: string;
  phone?: string | null;
  role?: string;
  cin?: string;
  address?: string | null;
  birthDate?: dayjs.Dayjs | null;
  municipalityId?: number | null;
}

export const defaultValue: Readonly<IAppUser> = {};
