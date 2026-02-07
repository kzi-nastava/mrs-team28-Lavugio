export interface ChatMessageModel {
  senderId: number;
  receiverId: number;
  text: string;
  timestamp: Date;
}