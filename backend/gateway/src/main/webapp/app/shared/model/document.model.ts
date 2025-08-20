import dayjs from 'dayjs';

export interface IDocument {
  id?: number;
  title?: string;
  type?: string;
  issueDate?: dayjs.Dayjs | null;
  file?: string;
  fileContentType?: string | null;
  citizenId?: number | null;
}

export const defaultValue: Readonly<IDocument> = {};
