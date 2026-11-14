import {Component,inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AssistantService} from '../core/assistant.service';

interface ChatMessage{role:'user'|'assistant';text:string}

@Component({
  selector:'app-assistant',
  standalone:true,
  imports:[FormsModule],
  template:`<section class="heading"><small>YOUR TRAVEL COMPANION</small><h1>Ask SoloTrail</h1><p>Get simple guidance for Chennai transport, stays, food, guides, and budgets.</p></section><div class="assistant-shell"><div class="chat-log">@for(message of messages;track $index){<div class="chat-message" [class.user-message]="message.role==='user'"><b>{{message.role==='user'?'You':'SoloTrail'}}</b><p>{{message.text}}</p></div>}</div><form class="chat-form" (ngSubmit)="send()"><input name="message" [(ngModel)]="draft" placeholder="Ask about your Chennai plan..." [disabled]="loading"><button class="button" [disabled]="!draft.trim()||loading">{{loading?'Thinking...':'Send'}}</button></form><small class="assistant-note">AI provider keys are optional. Without one, SoloTrail uses built-in travel guidance.</small></div>`
})
export class AssistantComponent{
  private service=inject(AssistantService);
  draft='';
  loading=false;
  messages:ChatMessage[]=[{role:'assistant',text:'Hi! I can help you plan transport, hotels, food, guides, budgets, and your demo checkout.'}];
  send(){
    const message=this.draft.trim();
    if(!message||this.loading)return;
    this.messages=[...this.messages,{role:'user',text:message}];
    this.draft='';
    this.loading=true;
    this.service.chat(message).subscribe({
      next:response=>{this.messages=[...this.messages,{role:'assistant',text:response.message}];this.loading=false},
      error:()=>{this.messages=[...this.messages,{role:'assistant',text:'The assistant is temporarily unavailable. Try again or browse the travel options directly.'}];this.loading=false}
    });
  }
}
