import { Pipe, PipeTransform } from '@angular/core';
import { Tag } from '../../models/tag.model';

@Pipe({ name: 'filterTags' })
export class FilterTagsPipe implements PipeTransform {
  transform(tags: Tag[]) {
    return tags.filter(t => !t.id.startsWith('ecoviz:is') && !t.id.startsWith('ecoviz:old'));
  }
}